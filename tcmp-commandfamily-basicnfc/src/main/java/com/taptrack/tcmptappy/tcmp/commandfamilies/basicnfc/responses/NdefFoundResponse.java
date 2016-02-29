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

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class NdefFoundResponse extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = 0x02;
    byte[] mTagCode;
    byte mTagType;
    NdefMessage mMessage;

    public NdefFoundResponse() {
        mTagCode = new byte[7];
        mMessage = new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY,null,null,null));
        mTagType = TagTypes.TAG_UNKNOWN;
    }

    public byte[] getTagCode() {
        return mTagCode;
    }

    public NdefMessage getNdefMessage() {
        return mMessage;
    }

    public byte getTagType() {
        return mTagType;
    }

    public NdefFoundResponse(byte[] payload) throws MalformedPayloadException {

        if(payload.length < 2) throw new MalformedPayloadException("No control bytes");

        mTagType = payload[0];
        byte tagCodeLength = (byte) (payload[1] & 0xff);
        mTagCode = Arrays.copyOfRange(payload, 2, tagCodeLength + 2);
        byte[] ndefMessage = new byte[payload.length - (tagCodeLength + 2)];

        //make sure ndef payload is not zero-length
        if(payload.length > ((tagCodeLength & 0xff)+2)) {
            System.arraycopy(payload,tagCodeLength+2,ndefMessage,0,(payload.length - tagCodeLength - 2));
        }
        if(ndefMessage.length != 0) {
            try {
                mMessage = new NdefMessage(ndefMessage);
            } catch (FormatException e) {
                e.printStackTrace();
                throw new MalformedPayloadException("Bad Ndef Format");
            }
        }
        else {
            mMessage = new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY,null,null,null));
        }
    }

    @Override
    public byte[] getPayload() {
        byte[] messageBytes = mMessage.toByteArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2+mTagCode.length+messageBytes.length);
        outputStream.write(mTagType);
        outputStream.write(mTagCode.length);
        try {
            outputStream.write(mTagCode);
            outputStream.write(messageBytes);
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
