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

package com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses;

import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;

public class BasicNfcLibraryVersionResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x04;
    byte mMajorVersion;
    byte mMinorVersion;

    public BasicNfcLibraryVersionResponse() {
        mMajorVersion = 0x00;
        mMinorVersion = 0x00;
    }
    public BasicNfcLibraryVersionResponse(byte majorVersion, byte minorVersion) {
        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
    }

    public byte getMajorVersion () {
        return mMajorVersion;
    }

    public byte getMinorVersion () {
        return mMinorVersion;
    }

    public BasicNfcLibraryVersionResponse(byte[] payload) throws MalformedPayloadException {
        if(payload.length != 2)
            throw new MalformedPayloadException();

        mMajorVersion = payload[0];
        mMinorVersion = payload[1];
    }

    @Override
    public byte[] getPayload() {
        return new byte[]{mMajorVersion,mMinorVersion};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
