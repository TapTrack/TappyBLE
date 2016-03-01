package com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands;

import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage;

public class ScanNdefCommand extends AbstractBasicNfcMessage {
    public static final byte COMMAND_CODE = (byte)0x04;
    protected byte mTimeout;

    public ScanNdefCommand() {
        mTimeout = (byte) 0x00;
    }

    public ScanNdefCommand(byte[] payload) {
        if(payload.length > 0)
            mTimeout = payload[0];
        else
            mTimeout = (byte) 0x00;
    }

    public ScanNdefCommand(byte timeout) {
        mTimeout = timeout;
    }

    public void setTimeout(byte timeout) {
        mTimeout = timeout;
    }

    public byte getTimeout() {
        return mTimeout;
    }

    @Override
    public byte[] getPayload() {
        return new byte[]{mTimeout};
    }

    @Override
    public byte getCommandCode() {
        return COMMAND_CODE;
    }
}
