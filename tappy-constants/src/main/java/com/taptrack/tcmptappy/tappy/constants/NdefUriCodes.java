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

package com.taptrack.tcmptappy.tappy.constants;

public interface NdefUriCodes {
    public static final byte URICODE_NOPREFIX = 0x00;
    public static final byte URICODE_HTTPWWW = 0x01;
    public static final byte URICODE_HTTPSWWW = 0x02;
    public static final byte URICODE_HTTP = 0x03;
    public static final byte URICODE_HTTPS = 0x04;
    public static final byte URICODE_TEL = 0x05;
    public static final byte URICODE_MAILTO = 0x06;
    public static final byte URICODE_FTP_ANON = 0x07;
    public static final byte URICODE_FTP_FTP = 0x08; // ftp://ftp
    public static final byte URICODE_FTPS = 0x09;
    public static final byte URICODE_SFTP = 0x0A;
    public static final byte URICODE_SMB = 0x0B;
    public static final byte URICODE_NFS = 0x0C;
    public static final byte URICODE_FTP = 0x0D;
    public static final byte URICODE_DAV = 0x0E;
    public static final byte URICODE_NEWS = 0x0F;
    public static final byte URICODE_TELNET = 0x10;
    public static final byte URICODE_IMAP = 0x11;
    public static final byte URICODE_RTSP = 0x12;
    public static final byte URICODE_URN = 0x13;
    public static final byte URICODE_POP = 0x14;
    public static final byte URICODE_SIP = 0x15;
    public static final byte URICODE_SIPS = 0x16;
    public static final byte URICODE_TFTP = 0x17;
    public static final byte URICODE_BTSPP = 0x18;
    public static final byte URICODE_BTL2CAP = 0x19;
    public static final byte URICODE_BTGOEP = 0x1A;
    public static final byte URICODE_TCPOBEX = 0x1B;
    public static final byte URICODE_IRDAOBEX = 0x1C;
    public static final byte URICODE_FILE = 0x1D;
    public static final byte URICODE_URN_EPC_ID = 0x1E;
    public static final byte URICODE_URN_EPC_TAG = 0x1F;
    public static final byte URICODE_URN_EPC_PAT = 0x20;
    public static final byte URICODE_URN_EPC_RAW = 0x21;
    public static final byte URICODE_URN_EPC = 0x22;
    public static final byte URICODE_URN_NFC = 0x23;
}
