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

package com.taptrack.tcmptappy.tcmp;


public class RawTCMPMessage extends TCMPMessage {
    protected byte[] mPayload;
    protected byte[] mFamily;
    protected byte mCommand;

    public RawTCMPMessage(byte[] message) throws TCMPMessageParseException {
        try {
            if(TCMPMessage.validate(message)) {
                mFamily = new byte[]{message[3], message[4]};
                mCommand = message[5];
                int length = message.length - 8;
                mPayload = new byte[length];
                System.arraycopy(message,6,mPayload,0,length);
//                mPayload = Arrays.copyOfRange(message, 6, message.length - 2);
            }
        } catch (MessageGarbledException e) {
            e.printStackTrace();
            throw new TCMPMessageParseException("Unable to parse, invalid TCMPMessage format",e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new TCMPMessageParseException("Unable to parse, invalid TCMPMessage format",e);
        }
    }

    @Override
    public byte[] getPayload() {
        return mPayload;
    }

    @Override
    public byte getCommandCode() {
        return mCommand;
    }

    @Override
    public byte[] getCommandFamily() {
        return mFamily;
    }
}
