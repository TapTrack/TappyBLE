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

package com.taptrack.tcmptappy.utils;

import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes;

public class NdefUriCodeUtils {
    public static String decodeNdefUri(byte ndefUriCode, byte[] message) {
        String prefix;

        switch(ndefUriCode) {
            case NdefUriCodes.URICODE_HTTPWWW:
                prefix = "http://www.";
                break;
            case NdefUriCodes.URICODE_HTTPSWWW:
                prefix = "https://www.";
                break;
            case NdefUriCodes.URICODE_HTTP:
                prefix = "http://";
                break;
            case NdefUriCodes.URICODE_HTTPS:
                prefix = "https://";
                break;
            case NdefUriCodes.URICODE_TEL:
                prefix = "tel:";
                break;
            case NdefUriCodes.URICODE_MAILTO:
                prefix = "mailto:";
                break;
            case NdefUriCodes.URICODE_FTP_ANON:
                prefix = "ftp://anonymous:anonymous@";
                break;
            case NdefUriCodes.URICODE_FTP_FTP:
                prefix = "ftp://ftp.";
                break; // ftp://ftp
            case NdefUriCodes.URICODE_FTPS:
                prefix = "ftps://";
                break;
            case NdefUriCodes.URICODE_SFTP:
                prefix = "sftp://";
                break;
            case NdefUriCodes.URICODE_SMB:
                prefix = "smb://";
                break;
            case NdefUriCodes.URICODE_NFS:
                prefix = "nfs://";
                break;
            case NdefUriCodes.URICODE_FTP:
                prefix = "ftp://";
                break;
            case NdefUriCodes.URICODE_DAV:
                prefix = "dav://";
                break;
            case NdefUriCodes.URICODE_NEWS:
                prefix = "news:";
                break;
            case NdefUriCodes.URICODE_TELNET:
                prefix = "telnet://";
                break;
            case NdefUriCodes.URICODE_IMAP:
                prefix = "imap:";
                break;
            case NdefUriCodes.URICODE_RTSP:
                prefix = "rtsp://";
                break;
            case NdefUriCodes.URICODE_URN:
                prefix = "urn:";
                break;
            case NdefUriCodes.URICODE_POP:
                prefix = "pop:";
                break;
            case NdefUriCodes.URICODE_SIP:
                prefix = "sip:";
                break;
            case NdefUriCodes.URICODE_SIPS:
                prefix = "sips:";
                break;
            case NdefUriCodes.URICODE_TFTP:
                prefix = "tftp:";
                break;
            case NdefUriCodes.URICODE_BTSPP:
                prefix = "btspp://";
                break;
            case NdefUriCodes.URICODE_BTL2CAP:
                prefix = "btl2cap://";
                break;
            case NdefUriCodes.URICODE_BTGOEP:
                prefix = "btgoep://";
                break;
            case NdefUriCodes.URICODE_TCPOBEX:
                prefix = "tcpobex://";
                break;
            case NdefUriCodes.URICODE_IRDAOBEX:
                prefix = "irdaobex://";
                break;
            case NdefUriCodes.URICODE_FILE:
                prefix = "file://";
                break;
            case NdefUriCodes.URICODE_URN_EPC_ID:
                prefix = "urn:epc:id:";
                break;
            case NdefUriCodes.URICODE_URN_EPC_TAG:
                prefix = "urn:epc:tag:";
                break;
            case NdefUriCodes.URICODE_URN_EPC_PAT:
                prefix = "urn:epc:pat:";
                break;
            case NdefUriCodes.URICODE_URN_EPC_RAW:
                prefix = "urn:epc:raw:";
                break;
            case NdefUriCodes.URICODE_URN_EPC:
                prefix = "urn:epc:";
                break;
            case NdefUriCodes.URICODE_URN_NFC:
                prefix = "urn:nfc:";
                break;
            default:
            case NdefUriCodes.URICODE_NOPREFIX:
                prefix = "";
                break;
        }

        return (prefix+new String(message));
    }
}
