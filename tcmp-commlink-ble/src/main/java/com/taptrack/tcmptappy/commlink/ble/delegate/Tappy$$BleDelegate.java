/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy.commlink.ble.delegate;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.taptrack.tcmptappy.commlink.PacketListener;
import com.taptrack.tcmptappy.tcmp.common.hdlc.HDLCByteArrayParser;
import com.taptrack.tcmptappy.tcmp.common.hdlc.HDLCParseResult;
import com.taptrack.tcmptappy.tcmp.common.hdlc.HDLCUtils;
import com.taptrack.tcmptappy.tcmp.common.hdlc.IllegalHDLCFormatException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Tappy$$BleDelegate {
    private static final String TAG = Tappy$$BleDelegate.class.getName();

    private final Set<PacketListener> packetListeners
            = new CopyOnWriteArraySet<>();

    private final Set<Tappy$$BleStatusChangedListener> statusListeners
            = new CopyOnWriteArraySet<>();

    private final AtomicInteger state = new AtomicInteger(Tappy$$BleState.DISCONNECTED);

    private final Context ctx;

    private final Tappy$$BleMtuChunkingStream mtuChunkingStream = new Tappy$$BleMtuChunkingStream();

    private final AtomicReference<BluetoothManager> bluetoothManagerRef = new AtomicReference<>();
    private final AtomicReference<BluetoothAdapter> bluetoothAdapterRef = new AtomicReference<>();
    private final AtomicReference<BluetoothGatt> bluetoothGattRef = new AtomicReference<>();


    private final String bleDeviceAddress;
    private final UUID serialServiceUuid;
    private final UUID rxCharactertisticUuid;
    private final UUID txCharacteristicUuid;

    private ByteArrayOutputStream receivedBuffer = new ByteArrayOutputStream();
    private final Object receivedBufferLock = new Object();

    private final AtomicBoolean isSending = new AtomicBoolean(false);

    private final Handler uiThreadHandler;

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                changeState(Tappy$$BleState.CONNECTED);
                BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                if(gatt != null) {
                    Log.i(TAG, "Attempting to start service discovery:" +
                            bluetoothGatt.discoverServices());
                }
                else {
                    Log.wtf(TAG,"Somehow connected with no gatt");
                    changeState(Tappy$$BleState.ERROR);
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                changeState(Tappy$$BleState.DISCONNECTED);
                Log.i(TAG, "Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                servicesDetected();
            } else {
                changeState(Tappy$$BleState.ERROR);
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristicRead(characteristic);
            }
            else if (status == BluetoothGatt.GATT_CONNECTION_CONGESTED) {
                // not logging an error as this may self resolve
                // probably not a good practise
                Log.e(TAG,"GATT CNXN CONGESTED");
            }
            else if (status == BluetoothGatt.GATT_FAILURE) {
                // not logging an error as this may self resolve
                // probably not a good practise, but google is super vague
                // about what this actually means
                Log.e(TAG,"GATT FAILURE");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            sendBytesFromBuffer();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            characteristicRead(characteristic);
        }
    };

    public Tappy$$BleDelegate (Context ctx,
                               String address,
                               UUID serialServiceUuid,
                               UUID txCharacteristicUuid,
                               UUID rxCharactertisticUuid) {
        this.ctx = ctx;
        this.uiThreadHandler = new Handler(ctx.getMainLooper());
        this.bleDeviceAddress = address;
        this.serialServiceUuid = serialServiceUuid;
        this.txCharacteristicUuid = txCharacteristicUuid;
        this.rxCharactertisticUuid = rxCharactertisticUuid;
    }

    private void characteristicRead(BluetoothGattCharacteristic characteristic) {
        if(characteristic.getUuid().equals(txCharacteristicUuid)) {
            final byte[] data = characteristic.getValue();

            List<byte[]> commands = new ArrayList<>(0);
            synchronized (receivedBufferLock) {
                try {
                    receivedBuffer.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(HDLCUtils.containsHdlcEndpoint(data)) {
                    byte[] currentBuffer = receivedBuffer.toByteArray();
                    HDLCParseResult parseResult = HDLCByteArrayParser.process(currentBuffer);
                    commands = parseResult.getPackets();
                    byte[] remainder = parseResult.getRemainder();
                    receivedBuffer = new ByteArrayOutputStream(remainder.length+mtuChunkingStream.getMtuLimit());
                    try {
                        receivedBuffer.write(remainder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (byte[] hdlcPacket : commands) {
                try {
                    byte[] decodedPacket = HDLCUtils.hdlcDecodePacket(hdlcPacket);
                    if (decodedPacket.length != 0) {
                        notifyListenersOfValidPacket(decodedPacket);
                    }
                } catch (IllegalHDLCFormatException e) {
                    notifyListenersOfInvalidPacket(hdlcPacket);
                }
            }
        }
    }

    public void registerPacketListener(PacketListener listener) {
        packetListeners.add(listener);
    }

    public void unregisterPacketListener(PacketListener listener) {
        packetListeners.remove(listener);
    }

    protected void notifyListenersOfValidPacket(byte[] packet) {
        for(PacketListener listener : packetListeners) {
            listener.onNewParsedPacket(packet);
        }
    }

    protected void notifyListenersOfInvalidPacket(byte[] packet) {
        for(PacketListener listener : packetListeners) {
            listener.onNewUnparsablePacket(packet);
        }
    }
    
    protected void notifyStateListeners(int newState) {
        for (Tappy$$BleStatusChangedListener listener:
             statusListeners) {
            listener.onNewStatus(newState);
        }
    }

    public void registerStatusChangedListener(Tappy$$BleStatusChangedListener listener) {
        statusListeners.add(listener);
    }

    public void unregisterStatusChangedListener(Tappy$$BleStatusChangedListener listener) {
        statusListeners.remove(listener);
    }

    private void changeState(int newState) {
        state.set(newState);
        notifyStateListeners(newState);
    }

    public boolean isReady() {
        return getState() == Tappy$$BleState.READY;
    }

    public void servicesDetected() {
        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
        if(bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(serialServiceUuid);
            BluetoothGattCharacteristic charac = service.getCharacteristic(txCharacteristicUuid);
            bluetoothGatt.setCharacteristicNotification(charac, true);
            changeState(Tappy$$BleState.READY);
        }
        else {
            Log.wtf(TAG,"Services detected with no gatt");
            changeState(Tappy$$BleState.ERROR);
        }
    }

    public int getState() {
        return state.get();
    }

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        BluetoothManager bluetoothManager = bluetoothManagerRef.get();
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                changeState(Tappy$$BleState.ERROR);
                return false;
            }
            else {
                bluetoothManagerRef.set(bluetoothManager);
            }
        }

        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            changeState(Tappy$$BleState.ERROR);
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        else {
            bluetoothAdapterRef.set(bluetoothAdapter);
        }

        return true;
    }

    public boolean connect() {
        changeState(Tappy$$BleState.DISCONNECTED);
        BluetoothAdapter bluetoothAdapter = bluetoothAdapterRef.get();
        if (bluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            changeState(Tappy$$BleState.ERROR);
            return false;
        }

        // Previously connected device.  Try to reconnect.
        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
        if (bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                //STATE CONNECTING
                changeState(Tappy$$BleState.CONNECTING);
                return true;
            } else {
                changeState(Tappy$$BleState.ERROR);
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bleDeviceAddress);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            changeState(Tappy$$BleState.ERROR);
            return false;
        }

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        // this supposedly has some strange behaviour on some devices
        changeState(Tappy$$BleState.CONNECTING);
        uiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothGatt gatt = device.connectGatt(ctx, false, gattCallback);
                bluetoothGattRef.set(gatt);
            }
        });
        Log.d(TAG, "Trying to create a new connection.");
        return true;
    }

    public void disconnect() {
        BluetoothAdapter bluetoothAdapter = bluetoothAdapterRef.get();
        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            changeState(Tappy$$BleState.ERROR);

            return;
        }
        bluetoothGatt.disconnect();
        changeState(Tappy$$BleState.DISCONNECTED);
    }

    public void close() {
        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();
        if(bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGattRef.set(null);
        changeState(Tappy$$BleState.CLOSED);
    }

    protected void initiateSendIfNecessary() {
        boolean sending = isSending.get();
        if(!isSending.getAndSet(true)) {
            sendBytesFromBuffer();
        }
    }

    protected void sendBytesFromBuffer() {
        int currentState = getState();

        BluetoothGatt bluetoothGatt = bluetoothGattRef.get();

        mtuChunkingStream.lockRead();
        if(mtuChunkingStream.hasBytes() &&
                currentState == Tappy$$BleState.READY &&
                bluetoothGatt != null) {
            byte[] nextChunk = mtuChunkingStream.getNextChunk();

            BluetoothGattService service = bluetoothGatt.getService(serialServiceUuid);
            if (service == null) {
                throw new IllegalStateException("Trying to send to device without truconnect");
            }

            BluetoothGattCharacteristic charac = service.getCharacteristic(rxCharactertisticUuid);

            if (nextChunk.length > 0) {
                charac.setValue(nextChunk);
                bluetoothGatt.writeCharacteristic(charac);
            }
            else {
                isSending.set(false);
            }
        }
        else {
            isSending.set(false);
        }
        mtuChunkingStream.unlockRead();
    }

    public void sendPacket(byte[] packet) {
        mtuChunkingStream.writeToBuffer(
                HDLCUtils.hdlcEncodePacket(packet));
        initiateSendIfNecessary();
    }

}
