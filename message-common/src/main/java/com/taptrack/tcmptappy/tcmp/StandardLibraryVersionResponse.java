package com.taptrack.tcmptappy.tcmp;

public interface StandardLibraryVersionResponse {
    byte getMajorVersion();
    void setMajorVersion(byte majorVersion);
    byte getMinorVersion();
    void setMinorVersion(byte minorVersion);
    void parsePayload(byte[] payload) throws MalformedPayloadException;
    byte[] getPayload();
}
