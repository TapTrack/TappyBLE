// IBleTappyMessageCallback.aidl
package com.taptrack.tcmptappy.tappy.ble.service;

// Declare any non-default types here with import statements
import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;

interface IBleTappyMessageCallback {
    oneway void onMessageReceived(in ParcelableTappyBleDeviceDefinition device, in byte[] message);
}
