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

/**
 * Holder for the device statuses.
 *
 * Note: not all statuses are called in all circumstances, so application behaviour shouldn't
 * necessarily be dependent on a certain order of calls.
 */
public final class TappyBleDeviceStatus {
    private TappyBleDeviceStatus() {
        //do not instantiate
    }

    public static final int UNKNOWN = 1;
    public static final int CONNECTING = 2;
    public static final int CONNECTED = 3;
    public static final int READY = 4;
    public static final int DISCONNECTING = 5;
    public static final int DISCONNECTED = 6;
    public static final int ERROR = 7;
}
