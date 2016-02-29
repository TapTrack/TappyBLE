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

package com.taptrack.tcmptappy.utils;

import android.os.Build;

public class BleUtils {
    /**
     * Retrieve the maximum number of BLE devices that this device can connect to.
     *
     * Warning, this is basically just conservative guesswork. Newer versions of android can usually support
     * more devices, but it depends on the phone's hardware and OS configuration at the end of the day.
     * @return Maximum number of ble devices
     */
    public static int getMaxBleCount() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return 3;
        }
        else {
            return 5;
        }
    }
}
