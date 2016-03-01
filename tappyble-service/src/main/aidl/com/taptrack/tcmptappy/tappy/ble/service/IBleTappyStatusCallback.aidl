// IBleTappyStatusCallback.aidl
package com.taptrack.tcmptappy.tappy.ble.service;

// Declare any non-default types here with import statements
import com.taptrack.tcmptappy.tappy.ble.ParcelableTappyBleDeviceDefinition;

interface IBleTappyStatusCallback {
    oneway void onTappyBleStatus(in ParcelableTappyBleDeviceDefinition device, int status);
}
