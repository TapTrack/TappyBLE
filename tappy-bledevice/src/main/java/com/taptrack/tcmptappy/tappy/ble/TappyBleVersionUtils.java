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

package com.taptrack.tcmptappy.tappy.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.os.Build;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

public class TappyBleVersionUtils {

    public static TappyBleDeviceDefinition getVersionForDevice(BluetoothDevice device) {
        if(TappyVersions.VersionOne.nameMatches(device)) {
            return TappyVersions.VersionOne.getTappyDeviceDefinition(device);
        }
        else {
            return null;
        }
    }

    public static List<com.taptrack.tcmptappy.blescannercompat.ScanFilter> getCompatScanFilter() {
        com.taptrack.tcmptappy.blescannercompat.ScanFilter.Builder builder =
                new com.taptrack.tcmptappy.blescannercompat.ScanFilter.Builder();
        builder.setServiceUuid(new ParcelUuid(TappyVersions.VersionOne.TRUCONNECT_SERVICE_UUID));
        List<com.taptrack.tcmptappy.blescannercompat.ScanFilter> list = new ArrayList<com.taptrack.tcmptappy.blescannercompat.ScanFilter>(1);
        list.add(builder.build());
        return list;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static List<ScanFilter> getScanFilter() {
        ScanFilter.Builder builder =
                new ScanFilter.Builder();
        builder.setServiceUuid(new ParcelUuid(TappyVersions.VersionOne.TRUCONNECT_SERVICE_UUID));
        List<ScanFilter> list = new ArrayList<ScanFilter>(1);
        list.add(builder.build());
        return list;
    }

}
