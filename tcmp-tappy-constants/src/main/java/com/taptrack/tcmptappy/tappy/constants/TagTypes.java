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

package com.taptrack.tcmptappy.tappy.constants;

public interface TagTypes {
    public static final byte TAG_UNKNOWN = (byte) 0x00;
    public static final byte MIFARE_ULTRALIGHT = (byte) 0x01;
    public static final byte NTAG203 = (byte) 0x02;
    public static final byte MIFARE_ULTRALIGHT_C = (byte) 0x03;
    public static final byte MIFARE_STD_1K = (byte) 0x04;
    public static final byte MIFARE_STD_4K = (byte) 0x05;
    public static final byte MIFARE_DESFIRE_EV1_2K = (byte) 0x06;
    public static final byte TYPE_2_TAG = (byte) 0x07;
    public static final byte MIFARE_PLUS_2K_CL2 = (byte) 0x08;
    public static final byte MIFARE_PLUS_4K_CL2 = (byte) 0x09;
    public static final byte MIFARE_MINI = (byte) 0x0A;
    public static final byte OTHER_TYPE4 = (byte) 0x0B;
    public static final byte MIFARE_DESFIRE_EV1_4K = (byte) 0xC;
    public static final byte MIFARE_DESFIRE_EV1_8K = (byte) 0x0D;
    public static final byte MIFARE_DESFIRE = (byte) 0x0E; //unspecified size
    public static final byte TOPAZ_512 = (byte) 0x0F; //put these definitions in here rather than type1.h to ensure no collisions
    public static final byte NTAG_210 = (byte) 0x10;
    public static final byte NTAG_212 = (byte) 0x11;
    public static final byte NTAG_213 = (byte) 0x12;
    public static final byte NTAG_215 = (byte) 0x13;
    public static final byte NTAG_216 = (byte) 0x14;
    public static final byte NO_TAG = (byte) 0xFF; //this should never happen, here for consistency
}
