package com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses;

import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.AbstractSystemMessage;

public class FirmwareVersionResponse extends AbstractSystemMessage {
    public static final byte COMMAND_CODE = 0x06;
    byte mMajorVersion;
    byte mMinorVersion;

    public FirmwareVersionResponse() {
        mMajorVersion = 0x00;
        mMinorVersion = 0x00;
    }
    public FirmwareVersionResponse(byte majorVersion, byte minorVersion) {
        mMajorVersion = majorVersion;
        mMinorVersion = minorVersion;
    }

    public byte getMajorVersion () {
        return mMajorVersion;
    }

    public byte getMinorVersion () {
        return mMinorVersion;
    }

    public FirmwareVersionResponse(byte[] payload) throws MalformedPayloadException {
        parsePayload(payload);
    }

    @Override
    public void parsePayload(byte[] payload) throws MalformedPayloadException {
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
