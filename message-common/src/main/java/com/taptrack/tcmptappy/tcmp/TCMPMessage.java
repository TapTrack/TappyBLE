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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public abstract class TCMPMessage {
    public abstract void parsePayload(byte[] payload) throws MalformedPayloadException;

    public abstract byte[] getPayload();

    public abstract byte getCommandCode();

    public abstract byte[] getCommandFamily();

    private  static byte[] calculateCRCBitwise(byte[] data) {
        int crc = 0x6363;
        for(int i = 0; i < data.length; ++i) {
            crc = update_cr16(crc, data[i]);
        }

        return shortToByteArray((short)crc);
    }


    private static int update_cr16(int crc, byte b) {
        int i, v, tcrc = 0;

        v = (int) ((crc ^ b) & 0xff);
        for(i = 0; i < 8; i++) {
            tcrc = (int) ((( (tcrc ^ v) & 1) != 0) ? (tcrc >> 1) ^ 0x8408 : tcrc >>1);
            v >>= 1;
        }

        return (int) (((crc >> 8) ^ tcrc) & 0xffff);
    }


    private static byte[] shortToByteArray(final short value) {
        return new byte[] { (byte) (value >>> 8), (byte) (value) };
    }

    public byte[] toByteArray() {
        byte[] data = getPayload();
        byte[] family = getCommandFamily();
        byte code = getCommandCode();

        int length = data.length + 5;
        byte l1 = (byte)((length >> 8) & 0xff);
        byte l2 = (byte)((length) & 0xff);
        byte lcs = (byte) ((((byte) 0xFF - (((l1 & 0xff) + (l2 & 0xff)) & 0xff)) + (0x01 & 0xff)) & 0xff);
        byte[] frame = new byte[0];
        byte[] packet = new byte[0];
        try {
            frame = concatByteArr(new byte[]{l1,l2,lcs},family,new byte[]{code},data);
            packet = concatByteArr(frame,calculateCRCBitwise(frame));
        } catch (IOException e) {
            e.printStackTrace();
            //wtf-level error if this happens
        }
        return packet;
    }

    private static byte[] concatByteArr(byte[]... byteArrays) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for(byte[] byteArray : byteArrays) {
            stream.write(byteArray);
        }
        return stream.toByteArray();
    }

    public static boolean validate(byte[] data) throws IllegalArgumentException, MessageGarbledException {
        if(data.length >= 8) {
            byte l1 = data[0];
            byte l2 = data[1];
            byte lcs = data[2];
            byte[] family = new byte[]{data[3],data[4]};
            byte command = data[5];
            byte[] crc = new byte[] {data[data.length - 2], data[data.length-1]};

            byte[] toCheckCRC = Arrays.copyOfRange(data,0,data.length-2);
            byte[] toCheckLength = Arrays.copyOfRange(data,3,data.length);
            byte[] calculatedCRC = calculateCRCBitwise(toCheckCRC);

            byte calculatedLcs = (byte) ((((byte) 0xFF - (((l1 & 0xff) + (l2 & 0xff)) & 0xff)) + (0x01 & 0xff)) & 0xff);

            if(calculatedLcs == lcs && Arrays.equals(crc,calculatedCRC)) {
                int expectedLength = ((l1 & 0xff) << 8) + (l2 & 0xff);
                if(expectedLength == toCheckLength.length) {
                    return true;
                }
                else {
                    throw new IllegalArgumentException("Body of command too short");
                }
            }
            else {
                throw new MessageGarbledException("Bad CRC or LCS");
            }
        }
        else {
            throw new IllegalArgumentException("Command too short");
        }
    }

}
