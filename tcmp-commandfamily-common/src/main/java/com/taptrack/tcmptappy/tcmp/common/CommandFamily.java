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

package com.taptrack.tcmptappy.tcmp.common;

import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;

public interface CommandFamily {
    /**
     * Parse a TCMP message into the explicit message that it represents. Generally used for
     * decoding sent TCMP messages
     * @param message message to decode, must not be null
     * @return TCMPMessage of explicit type
     * @throws com.taptrack.tcmptappy.commandfamily.CommandCodeNotSupportedException No command with the messages command code registered in this library
     * @throws MalformedPayloadException Command code valid, but payload is malformed
     */
    com.taptrack.tcmptappy.tcmp.TCMPMessage parseCommand(com.taptrack.tcmptappy.tcmp.TCMPMessage message) throws CommandCodeNotSupportedException, MalformedPayloadException;

    /**
     * Parse a TCMP message into the explicit message that it represents. Generally used for
     * decoding received TCMP messages
     * @param message message to decode, must not be null
     * @return TCMPMessage of explicit type
     * @throws com.taptrack.tcmptappy.commandfamily.CommandCodeNotSupportedException No response with the messages response code registered in this library
     * @throws MalformedPayloadException Response code valid, but payload is malformed
     */
    com.taptrack.tcmptappy.tcmp.TCMPMessage parseResponse(com.taptrack.tcmptappy.tcmp.TCMPMessage message) throws ResponseCodeNotSupportedException, MalformedPayloadException;

    /**
     * Get the two-byte command family identifier for this command family
     * @return byte array of length two representing the command family id
     */
    byte[] getCommandFamilyId();
}
