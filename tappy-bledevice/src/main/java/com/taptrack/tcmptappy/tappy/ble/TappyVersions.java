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

import android.bluetooth.BluetoothDevice;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TappyVersions {
    private TappyVersions() {};
    public static final  class VersionOne {
        private VersionOne() {};

        private static Pattern versionOne = Pattern.compile("^TAPPY[0-9][0-9][0-9]$");
        private static Pattern versionOneDebug = Pattern.compile("^TAPPYBLE$");

        public final static UUID TRUCONNECT_SERVICE_UUID
                = UUID.fromString("175f8f23-a570-49bd-9627-815a6a27de2a");
        public final static UUID TRUCONNECT_RX_CHARACTERISTIC_UUID
                = UUID.fromString("1cce1ea8-bd34-4813-a00a-c76e028fadcb");
        public final static UUID TRUCONNECT_TX_CHARACTERISTIC_UUID
                = UUID.fromString("cacc07ff-ffff-4c48-8fae-a9ef71b75e26");

        public static boolean nameMatches(BluetoothDevice device) {
            Matcher matcher = versionOne.matcher(device.getName());
            Matcher matcher2 = versionOneDebug.matcher(device.getName());
            if(matcher.matches() || matcher2.matches()) {
                return true;
            }
            return false;
        }

        public static TappyBleDeviceDefinition getTappyDeviceDefinition(BluetoothDevice device) {
            return new ParcelableTappyBleDeviceDefinition(device.getName(),device.getAddress(),
                    TRUCONNECT_SERVICE_UUID,
                    TRUCONNECT_RX_CHARACTERISTIC_UUID,
                    TRUCONNECT_TX_CHARACTERISTIC_UUID);
        }
    }
}
