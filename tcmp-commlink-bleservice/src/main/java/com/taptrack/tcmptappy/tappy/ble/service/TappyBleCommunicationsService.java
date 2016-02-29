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

package com.taptrack.tcmptappy.tappy.ble.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.taptrack.tcmptappy.commlink.CommunicatorStatusChangeListener;
import com.taptrack.tcmptappy.commlink.TcmpMessageListener;
import com.taptrack.tcmptappy.commlink.UnparsablePacketListener;
import com.taptrack.tcmptappy.commlink.ble.TappyBleCommunicator;
import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TappyBleCommunicationsService extends Service {
    private static final String TAG = TappyBleCommunicationsService.class.getSimpleName();

    private final IBleTappyServiceIPC.Stub binder = new TappyBleCommunicationsBinder(this);

    private Map<String,CommunicatorHolder> communicatorHolderMap = new ConcurrentHashMap<>();

    private final ReadWriteLock connectionsRwLock = new ReentrantReadWriteLock();
    private final Lock connectionsReadLock = connectionsRwLock.readLock();
    private final Lock connectionsEditLock = connectionsRwLock.writeLock();

    private AtomicReference<IBleTappyMessageCallback> receivedCallbackRef = new AtomicReference<>();
    private AtomicReference<IBleTappyMessageCallback> unparsableCallbackRef = new AtomicReference<>();
    private AtomicReference<IBleTappyStatusCallback> statusCallbackRef= new AtomicReference<>();

    protected static final class CommunicatorHolder {
        private final TappyBleCommunicator communicator;
        private final TappyBleDeviceDefinition deviceDefinition;

        public CommunicatorHolder(TappyBleCommunicator communicator, TappyBleDeviceDefinition deviceDefinition) {
            this.communicator = communicator;
            this.deviceDefinition = deviceDefinition;
        }

        public TappyBleCommunicator getCommunicator() {
            return communicator;
        }

        public TappyBleDeviceDefinition getDeviceDefinition() {
            return deviceDefinition;
        }
    }

    protected TappyBleCommunicator getCommunicator(TappyBleDeviceDefinition deviceDefinition) {
        connectionsReadLock.lock();
        CommunicatorHolder holder = communicatorHolderMap.get(deviceDefinition.getAddress());
        connectionsReadLock.unlock();
        return holder.getCommunicator();
    }

    public void sendMessage(ParcelableTappyBleDeviceDefinition definition, byte[] message) {
        TappyBleCommunicator communicator = getCommunicator(definition);
        if(communicator != null) {
            communicator.sendTcmpMessage(message);
        }
    }

    public void broadcastMessage(byte[] message) {
        for(CommunicatorHolder communicatorHolder : communicatorHolderMap.values()) {
            communicatorHolder.getCommunicator().sendTcmpMessage(message);
        }
    }

    public void setMessageReceivedCallback(IBleTappyMessageCallback callback) {
        receivedCallbackRef.set(callback);
    }

    public void setUnparsableMessageReceivedCallback(IBleTappyMessageCallback callback) {
        unparsableCallbackRef.set(callback);
    }

    public int getTappyStatusSync(ParcelableTappyBleDeviceDefinition definition) {
        TappyBleCommunicator communicator = getCommunicator(definition);
        if(communicator == null)
            return TappyBleDeviceStatus.UNKNOWN;
        else
            return communicator.getState();
    }

    public void getTappyStatus(ParcelableTappyBleDeviceDefinition definition, IBleTappyStatusCallback cb) {
        int state = TappyBleDeviceStatus.UNKNOWN;

        TappyBleCommunicator communicator = getCommunicator(definition);
        if(communicator != null)
            state = communicator.getState();
        try {
            cb.onTappyBleStatus(definition, state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setOnTappyStatusChangedListener(IBleTappyStatusCallback cb) {
        statusCallbackRef.set(cb);
    }

    public void connectDevice(ParcelableTappyBleDeviceDefinition definition) {
        connectionsEditLock.lock();
        CommunicatorHolder oldCommunicatorHolder = communicatorHolderMap.get(definition.getAddress());
        if(oldCommunicatorHolder == null) {
            TappyBleCommunicator newCommunicator = new TappyBleCommunicator(getApplicationContext(), definition);

            newCommunicator.registerStatusChangedListener(
                    new TappyStatusChangedListener(definition));
            newCommunicator.registerMessageReceivedListener(
                    new TappyMessageReceivedListener(definition));
            newCommunicator.registerUnparsableMessageReceivedListener(
                    new TappyUnparsableMessageReceivedListener(definition));

            communicatorHolderMap.put(definition.getAddress(),new CommunicatorHolder(newCommunicator,definition));
            newCommunicator.initialize();
            newCommunicator.connect();
        }
        else {
            TappyBleCommunicator oldCommunicator = oldCommunicatorHolder.getCommunicator();
            int commState = oldCommunicator.getState();
            if (commState == TappyBleDeviceStatus.ERROR){
                oldCommunicator.initialize();
                oldCommunicator.connect();
            }
            else if(commState != TappyBleDeviceStatus.READY) {
                oldCommunicator.connect();
            }
        }
        connectionsEditLock.unlock();
    }


    public void disconnectDevice(ParcelableTappyBleDeviceDefinition definition) {
        connectionsEditLock.lock();

        CommunicatorHolder communicatorHolder = communicatorHolderMap.get(definition.getAddress());
        if(communicatorHolder != null) {
            communicatorHolder.getCommunicator().close();
            communicatorHolderMap.remove(definition.getAddress());

            IBleTappyStatusCallback statusCallback = statusCallbackRef.get();
            if (statusCallback != null) {
                try {
                    statusCallback.onTappyBleStatus(new ParcelableTappyBleDeviceDefinition(definition),
                            TappyBleDeviceStatus.DISCONNECTED);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error on informing of tappy disconnection", e);
                }
            }
        }

        connectionsEditLock.unlock();
    }

    public void disconnectAll()  {
        connectionsEditLock.lock();

        Iterator<Map.Entry<String,CommunicatorHolder>> it = communicatorHolderMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String,CommunicatorHolder> item = it.next();

            TappyBleCommunicator communicator = item.getValue().getCommunicator();
            TappyBleDeviceDefinition deviceDefinition = item.getValue().getDeviceDefinition();

            it.remove();

            communicator.close();

            IBleTappyStatusCallback statusCallback = statusCallbackRef.get();
            if(statusCallback != null) {
                try {
                    statusCallback.onTappyBleStatus(new ParcelableTappyBleDeviceDefinition(deviceDefinition),
                            TappyBleDeviceStatus.DISCONNECTED);
                } catch (RemoteException e) {
                    Log.e(TAG,"Error on informing of tappy disconnection",e);
                }
            }
        }

        connectionsEditLock.unlock();
    }

    public void unregisterListeners() {
        setOnTappyStatusChangedListener(null);
        setUnparsableMessageReceivedCallback(null);
        setMessageReceivedCallback(null);
    }

    public void close() throws RemoteException {
        disconnectAll();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            close();
        } catch (RemoteException ignored) {

        }
    }

    protected class TappyMessageReceivedListener implements TcmpMessageListener {
        protected final ParcelableTappyBleDeviceDefinition deviceDefinition;

        public TappyMessageReceivedListener(TappyBleDeviceDefinition deviceDefinition) {
            this.deviceDefinition = new ParcelableTappyBleDeviceDefinition(deviceDefinition);
        }

        @Override
        public void onNewTcmpMessage(TCMPMessage tcmpMessage) {
            IBleTappyMessageCallback receivedCallback = receivedCallbackRef.get();
            try {
                if(receivedCallback != null)
                    receivedCallback.onMessageReceived(deviceDefinition, tcmpMessage.toByteArray());
            } catch (RemoteException e) {
                Log.e(TAG, "Error on informing of tappy message received", e);
            }
        }
    }

    protected class TappyUnparsableMessageReceivedListener implements UnparsablePacketListener {
        protected final ParcelableTappyBleDeviceDefinition deviceDefinition;

        public TappyUnparsableMessageReceivedListener(TappyBleDeviceDefinition deviceDefinition) {
            this.deviceDefinition = new ParcelableTappyBleDeviceDefinition(deviceDefinition);
        }

        @Override
        public void onUnparsablePacket(byte[] packet) {
            IBleTappyMessageCallback unparsableCallback = unparsableCallbackRef.get();
            try {
                if(unparsableCallback != null)
                    unparsableCallback.onMessageReceived(deviceDefinition,packet);
            } catch (RemoteException e) {
                Log.e(TAG, "Error on informing of unparsable tappy message received", e);
            }
        }
    }

    protected class TappyStatusChangedListener implements CommunicatorStatusChangeListener {
        protected final ParcelableTappyBleDeviceDefinition deviceDefinition;

        public TappyStatusChangedListener(TappyBleDeviceDefinition deviceDefinition) {
            this.deviceDefinition = new ParcelableTappyBleDeviceDefinition(deviceDefinition);
        }

        @Override
        public void onStatusChanged(int newStatus) {
            IBleTappyStatusCallback statusCallback = statusCallbackRef.get();
            try {
                if(statusCallback != null)
                    statusCallback.onTappyBleStatus(deviceDefinition, newStatus);
            } catch (RemoteException e) {
                Log.e(TAG, "Error on informing of unparsable tappy status changed received", e);
            }
        }
    }
}
