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

package com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage;

public class StreamNdefCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x03;
    protected byte mDuration;

    public StreamNdefCommand() {
        mDuration = (byte) 0x00;
    }

    public StreamNdefCommand(byte[] payload) {
        if(payload.length > 0)
            mDuration = payload[0];
        else
            mDuration = (byte) 0x00;
    }

    public StreamNdefCommand(byte duration) {
        mDuration = duration;
    }

    public void setDuration(byte duration) {
        mDuration = duration;
    }

    public byte getDuration() {
        return mDuration;
    }

    @Override
    public byte[] getPayload() {
        return new byte[]{mDuration};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
