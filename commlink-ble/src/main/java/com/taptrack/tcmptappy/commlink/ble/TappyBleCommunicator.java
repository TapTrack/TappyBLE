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

package com.taptrack.tcmptappy.commlink.ble;

import android.content.Context;
import android.util.Log;

import com.taptrack.tcmptappy.commlink.CommunicatorStatusChangeListener;
import com.taptrack.tcmptappy.commlink.PacketListener;
import com.taptrack.tcmptappy.commlink.TcmpMessageListener;
import com.taptrack.tcmptappy.commlink.UnparsablePacketListener;
import com.taptrack.tcmptappy.commlink.ble.delegate.TappyBleDelegate;
import com.taptrack.tcmptappy.commlink.ble.delegate.TappyBleState;
import com.taptrack.tcmptappy.commlink.ble.delegate.TappyBleStatusChangedListener;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus;
import com.taptrack.tcmptappy.tcmp.RawTCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessageParseException;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

public class TappyBleCommunicator implements TappyBleStatusChangedListener, PacketListener {
    private static final String TAG = TappyBleCommunicator.class.getSimpleName();

    private final Set<TcmpMessageListener> receivedListeners
            = new CopyOnWriteArraySet<>();
    private final Set<UnparsablePacketListener> unparsablePacketListeners
            = new CopyOnWriteArraySet<>();

    private final Set<CommunicatorStatusChangeListener> communicatorStatusChangeListeners
            = new CopyOnWriteArraySet<>();

    private final TappyBleDelegate tappyBleDelegate;

    private final TappyBleDeviceDefinition deviceDefinition;

    private final AtomicInteger bleState = new AtomicInteger();

    public TappyBleCommunicator(Context ctx, TappyBleDeviceDefinition deviceDefinition) {
        tappyBleDelegate =
                new TappyBleDelegate(ctx,
                        deviceDefinition.getAddress(),
                        deviceDefinition.getSerialServiceUuid(),
                        deviceDefinition.getTxCharacteristicUuid(),
                        deviceDefinition.getRxCharacteristicUuid());


        tappyBleDelegate.registerPacketListener(this);
        tappyBleDelegate.registerStatusChangedListener(this);
        bleState.set(tappyBleDelegate.getState());
        this.deviceDefinition = deviceDefinition;
    }

    public void initialize() {
        tappyBleDelegate.initialize();
    }

    public void connect() {
        tappyBleDelegate.connect();
    }

    public void close() {
        tappyBleDelegate.close();
        receivedListeners.clear();
        communicatorStatusChangeListeners.clear();
        unparsablePacketListeners.clear();
    }

    /**
     * Gets a state according to {@link com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus} resolved
     * from the BLE state
     * @return current {@link com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus}
     */
    public int getState() {
        int state = readStateSync();
        switch (state) {
            case TappyBleState.CONNECTED: {
                return TappyBleDeviceStatus.CONNECTING;
            }
            case TappyBleState.CONNECTING: {
                return TappyBleDeviceStatus.CONNECTING;
            }
            case TappyBleState.CLOSED:
            case TappyBleState.DISCONNECTED: {
                return TappyBleDeviceStatus.DISCONNECTED;
            }
            case TappyBleState.READY: {
                return TappyBleDeviceStatus.READY;
            }
            default: {
                return TappyBleDeviceStatus.ERROR;
            }
        }
    }

    protected int readStateSync() {
        return bleState.get();
    }

    public void sendTcmpMessage(TCMPMessage tcmpMessage) {
        tappyBleDelegate.sendPacket(tcmpMessage.toByteArray());
    }

    public void sendTcmpMessage(byte[] tcmpMessage) {
        tappyBleDelegate.sendPacket(tcmpMessage);
    }

    public void registerMessageReceivedListener(TcmpMessageListener listener) {
        receivedListeners.add(listener);
    }

    public void unregisterMessageReceivedListener(TcmpMessageListener listener) {
        receivedListeners.remove(listener);
    }

    protected void notifyReceivedMessageListeners(TCMPMessage msg) {
        for(TcmpMessageListener listener : receivedListeners) {
            listener.onNewTcmpMessage(msg);
        }
    }

    public void registerUnparsableMessageReceivedListener(UnparsablePacketListener listener) {
        unparsablePacketListeners.add(listener);
    }

    public void unregisterUnparsableMessageReceivedListener(UnparsablePacketListener listener) {
        unparsablePacketListeners.remove(listener);
    }

    protected void notifyUnparsableMessageListeners(byte[] garbledPacket) {
        for(UnparsablePacketListener listener : unparsablePacketListeners) {
            listener.onUnparsablePacket(garbledPacket);
        }
    }

    public void registerStatusChangedListener(CommunicatorStatusChangeListener listener) {
        communicatorStatusChangeListeners.add(listener);
    }

    public void unregisterStatusChangedListener(CommunicatorStatusChangeListener listener) {
        communicatorStatusChangeListeners.remove(listener);
    }

    protected void notifyStatusChangedListeners(int newState) {
        for (CommunicatorStatusChangeListener listener :
                communicatorStatusChangeListeners) {
            listener.onStatusChanged(newState);
        }
    }

    @Override
    public void onNewStatus(int status) {
        bleState.set(status);
        notifyStatusChangedListeners(getState());
    }

    @Override
    public void onNewParsedPacket(byte[] packet) {
        try {
            RawTCMPMessage rawTCMPMessage = new RawTCMPMessage(packet);
            notifyReceivedMessageListeners(rawTCMPMessage);
        } catch (TCMPMessageParseException e) {
            Log.w(TAG,"Unparsable TCMP packet received",e);
            onNewUnparsablePacket(packet);
        }
    }

    @Override
    public void onNewUnparsablePacket(byte[] packet) {
        notifyUnparsableMessageListeners(packet);
    }
}
