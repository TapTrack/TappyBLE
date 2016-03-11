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

import android.os.RemoteException;

import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;

import java.util.List;

public class TappyBleCommunicationsBinder extends IBleTappyServiceIPC.Stub {
    private final TappyBleCommunicationsService service;

    public TappyBleCommunicationsBinder(TappyBleCommunicationsService service) {
        this.service = service;
    }

    @Override
    public void sendMessage(ParcelableTappyBleDeviceDefinition definition, byte[] message) throws RemoteException {
        service.sendMessage(definition,message);
    }

    @Override
    public void broadcastMessage(byte[] message) throws RemoteException {
        service.broadcastMessage(message);
    }

    @Override
    public void setMessageReceivedCallback(IBleTappyMessageCallback callback) throws RemoteException {
        service.setMessageReceivedCallback(callback);
    }

    @Override
    public void unregisterMessageReceivedCallback() throws RemoteException {
        service.setMessageReceivedCallback(null);
    }

    @Override
    public void setUnparsableMessageReceivedCallback(IBleTappyMessageCallback callback) throws RemoteException {
        service.setUnparsableMessageReceivedCallback(callback);
    }

    @Override
    public void unregisterUnparsableMessageReceivedCallback() throws RemoteException {
        service.setUnparsableMessageReceivedCallback(null);
    }

    @Override
    public int getTappyStatusSync(ParcelableTappyBleDeviceDefinition definition) throws RemoteException {
        return service.getTappyStatusSync(definition);
    }

    @Override
    public void getTappyStatus(ParcelableTappyBleDeviceDefinition definition, IBleTappyStatusCallback cb) throws RemoteException {
        service.getTappyStatus(definition, cb);
    }

    @Override
    public void setOnTappyStatusChangedListener(IBleTappyStatusCallback cb) throws RemoteException {
        service.setOnTappyStatusChangedListener(cb);
    }

    @Override
    public void unregisterOnTappyStatusChangedListener() throws RemoteException {
        service.setOnTappyStatusChangedListener(null);
    }

    @Override
    public void connectDevice(ParcelableTappyBleDeviceDefinition definition) throws RemoteException {
        service.connectDevice(definition);
    }

    @Override
    public void setConnectedDevices(List<ParcelableTappyBleDeviceDefinition> devices) throws RemoteException {
        service.setConnectedDevices(devices);
    }

    @Override
    public void disconnectDevice(ParcelableTappyBleDeviceDefinition definition) throws RemoteException {
        service.disconnectDevice(definition);
    }

    @Override
    public void disconnectAll() throws RemoteException {
        service.disconnectAll();
    }

    @Override
    public void close() throws RemoteException {
        service.close();
    }
}
