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

public class ScanTagCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x02;
    protected byte mTimeout;
    protected byte mType;

    public ScanTagCommand() {
        mType = (byte) 0x02;
        mTimeout = (byte) 0x00;
    }

    public ScanTagCommand(byte[] payload) {
        if(payload.length >= 2) {
            mTimeout = payload[0];
            mType = payload[1];
        }
        else if (payload.length > 0) {
            mType = (byte) 0x02;
            mTimeout = payload[0];
        }
        else {
            mType = (byte) 0x02;
            mTimeout = (byte) 0x00;
        }
    }

    public ScanTagCommand(byte timeout) {
        mTimeout = timeout;
        mType = (byte) 0x02;
    }


    public ScanTagCommand(byte timeout, byte type) {
        mTimeout = timeout;
        mType = type;
    }

    public void setTimeout(byte timeout) {
        mTimeout = timeout;
    }

    public byte getTimeout() {
        return mTimeout;
    }

    public byte getType() {
        return mType;
    }

    public void setType(byte type) {
        mType = type;
    }

    @Override
    public byte[] getPayload() {
        return new byte[]{mTimeout,mType};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
