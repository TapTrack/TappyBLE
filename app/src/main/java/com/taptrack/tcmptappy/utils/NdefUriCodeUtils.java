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
        String uri;
        switch(ndefUriCode) {
            case NdefUriCodes.URICODE_HTTPWWW: {
                uri = "http://www.";
                break;
            }
            case NdefUriCodes.URICODE_HTTPSWWW: {
                uri = "https://www.";
                break;
            }
            case NdefUriCodes.URICODE_HTTP: {
                uri = "http://";
                break;
            }
            case NdefUriCodes.URICODE_HTTPS: {
                uri = "https://";
                break;
            }
            case NdefUriCodes.URICODE_TEL: {
                uri = "tel:";
                break;
            }
            case NdefUriCodes.URICODE_MAILTO: {
                uri = "mailto:";
                break;
            }
            default:
            case NdefUriCodes.URICODE_NOPREFIX: {
                uri = "";
                break;
            }
        }
        return (uri+new String(message));
    }
}
