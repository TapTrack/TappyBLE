package com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class WriteNdefUriRecordCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x05;
    protected byte duration;
    protected byte lockflag; //1 to lock the flag
    protected byte uriCode;
    protected byte[] uri;

    public WriteNdefUriRecordCommand() {
        duration = (byte) 0x00;
        lockflag = (byte) 0x00;
        uriCode = NdefUriCodes.URICODE_NOPREFIX;
        uri = new byte[0];
    }

    public WriteNdefUriRecordCommand(byte[] payload) {
        if(payload.length >= 3) {
            duration = payload[0];
            lockflag = payload[1];
            uriCode = payload[2];
            if(payload.length > 3) {
                uri = Arrays.copyOfRange(payload, 3, payload.length);
            }
            else {
                uri = new byte[0];
            }
        }
        else {
            throw new IllegalArgumentException("Invalid raw message");
        }
    }

    public WriteNdefUriRecordCommand(byte duration, boolean lockTag, byte uriCode, byte[] uri) {
        this.duration = duration;
        this.lockflag = (byte) (lockTag ? 0x01: 0x00);
        this.uri = uri;
        this.uriCode = uriCode;
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

    public byte[] getUri() {
        return uri;
    }

    public void setUri(byte[] uri) {
        this.uri = uri;
    }

    public byte getUriCode() {
        return uriCode;
    }

    public void setUriCode(byte uriCode) {
        this.uriCode = uriCode;
    }

    @Override
    public byte[] getPayload() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(3+uri.length);
        outputStream.write(duration);
        outputStream.write(lockflag);
        outputStream.write(uriCode);
        try {
            outputStream.write(uri);
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
