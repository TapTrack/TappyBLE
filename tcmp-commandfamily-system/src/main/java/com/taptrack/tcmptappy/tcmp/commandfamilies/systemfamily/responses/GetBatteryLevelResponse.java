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

package com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses;

import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.AbstractSystemMessage;

public class GetBatteryLevelResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = (byte) 0x08;
    private byte batteryLevel;

    public GetBatteryLevelResponse() {
        batteryLevel = 0x00;
    }

    public GetBatteryLevelResponse(byte[] payload) {
        if(payload.length != 1)
            throw new IllegalArgumentException("Bad parse");

        this.batteryLevel = payload[0];
    }

    public GetBatteryLevelResponse(byte batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public byte getBatteryLevel() {
        return this.batteryLevel;
    }

    public int getBatteryLevelPercent() {
        return (batteryLevel & 0xFF);
    }

    @Override
    public byte[] getPayload() {
        return new byte[0];
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}

