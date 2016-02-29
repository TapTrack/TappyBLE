// IBleTappyServiceIPC.aidl
package com.taptrack.tcmptappy.tappy.ble.service;

// Declare any non-default types here with import statements
import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyMessageCallback;
import com.taptrack.tcmptappy.tappy.ble.service.IBleTappyStatusCallback;

interface IBleTappyServiceIPC {
    oneway void sendMessage(in ParcelableTappyBleDeviceDefinition definition, in byte[] message);
    oneway void broadcastMessage(in byte[] message);
    oneway void setMessageReceivedCallback(IBleTappyMessageCallback callback);
    oneway void unregisterMessageReceivedCallback();
    oneway void setUnparsableMessageReceivedCallback(IBleTappyMessageCallback callback);
    oneway void unregisterUnparsableMessageReceivedCallback();

   //using this name due to aidl limitations on overloading
   int getTappyStatusSync(in ParcelableTappyBleDeviceDefinition definition);
   oneway void getTappyStatus(in ParcelableTappyBleDeviceDefinition definition, IBleTappyStatusCallback cb);
   oneway void setOnTappyStatusChangedListener(IBleTappyStatusCallback cb);
   oneway void unregisterOnTappyStatusChangedListener();

    oneway void connectDevice(in ParcelableTappyBleDeviceDefinition definition);
    oneway void disconnectDevice(in ParcelableTappyBleDeviceDefinition definition);
    oneway void disconnectAll();

    oneway void close();
}
