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

package com.taptrack.tcmptappy.blescannercompat;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.taptrack.tcmptappy.blescannercompat.scanners.jellybean.JellybeanBleScanner;
import com.taptrack.tcmptappy.blescannercompat.scanners.lollipop.LollipopBleScanner;

public abstract class BluetoothLeScannerCompat {
    private static IBluetoothLeScanner scannerImpl;
    private static final Object scannerLock = new Object();

    @NonNull
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH})
    public static IBluetoothLeScanner getBluetoothLeScanner () {
        synchronized (scannerLock) {
            if(scannerImpl == null) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    scannerImpl = new LollipopBleScanner();
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    scannerImpl = new JellybeanBleScanner();
                }
            }
        }

        if(scannerImpl == null)
            throw new IllegalStateException("This device has a version of Android that is too old to support BLE");
        else
            return scannerImpl;
    }



}
