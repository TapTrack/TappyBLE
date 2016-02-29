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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class WriteNdefCustomRecordCommand extends AbstractBasicNfcMessage {

    public static final byte COMMAND_CODE = (byte)0x07;
    protected byte duration;
    protected byte lockflag; //1 to lock the flag
    protected byte[] content;

    public WriteNdefCustomRecordCommand() {
        duration = (byte) 0x00;
        lockflag = (byte) 0x00;
        content = new byte[0];
    }

    public WriteNdefCustomRecordCommand(byte[] payload) {
        if(payload.length >= 2) {
            duration = payload[0];
            lockflag = payload[1];
            if(payload.length > 2) {
                content = Arrays.copyOfRange(payload, 2, payload.length);
            }
            else {
                content = new byte[0];
            }
        }
        else {
            throw new IllegalArgumentException("Invalid raw message");
        }
    }

    public WriteNdefCustomRecordCommand(byte duration, boolean lockTag, byte[] content) {
        this.duration = duration;
        this.lockflag = (byte) (lockTag ? 0x01: 0x00);
        this.content = content;
    }

    public void setDuration(byte duration) {
        this.duration = duration;
    }

    public byte getDuration() {
        return duration;
    }

    public boolean willLock() {
        return lockflag == 0x01;
    }

    public void setToLock(boolean lockTag) {
        this.lockflag = (byte) (lockTag ? 0x01:0x00);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }


    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(3+ content.length);
        outputStream.write(duration);
        outputStream.write(lockflag);
        try {
            outputStream.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
