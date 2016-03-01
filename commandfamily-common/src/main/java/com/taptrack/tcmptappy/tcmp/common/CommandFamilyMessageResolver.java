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
import com.taptrack.tcmptappy.tcmp.TCMPMessage;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandFamilyMessageResolver {
    private final Map<WrappedCommandFamilyId, CommandFamily> libraries;

    public CommandFamilyMessageResolver () {
        //initializing to two because system and tappy are standard
        libraries = new ConcurrentHashMap<WrappedCommandFamilyId, CommandFamily>(2);
    }

    /**
     * Register a new command library for dispatching.
     * @param newLibrary library to add for dispatch.
     * @throws NullPointerException if tappyCommandLibrary is null
     * @throws IllegalArgumentException if a command library is already registered with the
     * same command family id.
     */
    public void registerCommandLibrary(CommandFamily newLibrary) {
        registerCommandLibrary(newLibrary, false);
    }

    /**
     * Register a new command library for dispatching.
     * @param newLibrary library to add for dispatch.
     * @param overwriteExisting overwrite on conflicts
     * @throws NullPointerException if tappyCommandLibrary is null
     * @throws IllegalArgumentException if a command library is already registered with the
     * same command family id and overwriteExisting is set to false
     */
    public void registerCommandLibrary(CommandFamily newLibrary, boolean overwriteExisting) {
        if(newLibrary == null)
            throw new NullPointerException();
        byte[] familyId = newLibrary.getCommandFamilyId();
        WrappedCommandFamilyId wrappedId = new WrappedCommandFamilyId(familyId);
        if(libraries.containsKey(wrappedId) && !overwriteExisting) {
            throw new IllegalArgumentException("A command family has already been registered with that command code");
        }
        else {
            libraries.put(wrappedId,newLibrary);
        }
    }

    /**
     * Find the command library for a given message.
     * @param message The message to find the command library for. Must not be null;
     * @return the appropriate command library if found.
     * @throws FamilyCodeNotSupportedException if no library is found
     * @throws IllegalArgumentException if a TCMPMessage with no command family is supplied
     */
    private CommandFamily getCommandLibraryForMessage(TCMPMessage message) throws FamilyCodeNotSupportedException {
        if(message.getCommandFamily() == null)
            throw new IllegalArgumentException("Must have a command family");
        WrappedCommandFamilyId wrappedId = new WrappedCommandFamilyId(message.getCommandFamily());
        CommandFamily lib = libraries.get(wrappedId);
        if(lib == null)
            throw new FamilyCodeNotSupportedException(
                    String.format("No command library found for family code %s", bytesToHex(message.getCommandFamily())));
        return lib;
    }


    /**
     * From stack overflow question 9655181
     */
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Parse a TCMP command message into the specific message sent. Generally used fpr determining the type of unknown messages.
     * @param message Message to convert into specific message, must not be null
     * @return specific TCMP command for message if found
     * @throws FamilyCodeNotSupportedException No CommandFamily registered for family code
     * @throws CommandCodeNotSupportedException CommandFamily doesnt support the command code
     * @throws MalformedPayloadException The payload is malformed for the type of command the message represents
     */
    public TCMPMessage parseCommand(TCMPMessage message) throws FamilyCodeNotSupportedException, CommandCodeNotSupportedException, MalformedPayloadException {
        if(message == null)
            throw new NullPointerException();

        CommandFamily commandFamily = getCommandLibraryForMessage(message);

        return commandFamily.parseCommand(message);
    }

    /**
     * Parse a TCMP response message into the specific message sent. Generally used fpr determining the type of unknown messages.
     * @param message Message to convert into specific message, must not be null
     * @return specific TCMP command for message if found
     * @throws FamilyCodeNotSupportedException No CommandFamily registered for family code
     * @throws ResponseCodeNotSupportedException CommandFamily doesnt support the command code
     * @throws MalformedPayloadException The payload is malformed for the type of command the message represents
     */
    public TCMPMessage parseResponse(TCMPMessage message) throws FamilyCodeNotSupportedException, ResponseCodeNotSupportedException, MalformedPayloadException {
        if(message == null)
            throw new NullPointerException();

        CommandFamily commandFamily = getCommandLibraryForMessage(message);
        return commandFamily.parseResponse(message);
    }

    private static class WrappedCommandFamilyId {
        private final byte[] familyId;

        public WrappedCommandFamilyId(byte[] familyId) {
            if(familyId == null)
                throw new NullPointerException();
            this.familyId = familyId;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(familyId);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof WrappedCommandFamilyId) {
                return Arrays.equals(familyId,((WrappedCommandFamilyId) obj).familyId);
            }
            return false;
        }
    }
}
