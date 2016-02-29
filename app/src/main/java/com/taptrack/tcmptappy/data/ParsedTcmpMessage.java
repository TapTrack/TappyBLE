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

package com.taptrack.tcmptappy.data;

import android.support.annotation.NonNull;

import com.taptrack.tcmptappy.tcmp.common.CommandCodeNotSupportedException;
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;
import com.taptrack.tcmptappy.tcmp.RawTCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessageParseException;
import com.taptrack.tcmptappy.tcmp.common.FamilyCodeNotSupportedException;
import com.taptrack.tcmptappy.tcmp.common.ResponseCodeNotSupportedException;

import timber.log.Timber;

public class ParsedTcmpMessage {
    private SavedTcmpMessage savedMessage;
    private TCMPMessage rawTcmpMessage;
    private TCMPMessage resolvedTcmpMessage;
    private CommandFamilyMessageResolver resolver;

    public ParsedTcmpMessage(@NonNull SavedTcmpMessage message) {
        this.savedMessage = message;
    }

    public void setCommandFamilyResolver(CommandFamilyMessageResolver resolver) {
        this.resolvedTcmpMessage = null;
        this.resolver = resolver;
    }

    public SavedTcmpMessage getSavedMessage() {
        return savedMessage;
    }

    public TCMPMessage getRawTcmpMessage() {
        if(rawTcmpMessage == null) {
            try {
                rawTcmpMessage = new RawTCMPMessage(savedMessage.getMessage());
            } catch (TCMPMessageParseException e) {
                e.printStackTrace();
            }
        }
        return rawTcmpMessage;
    }

    public TCMPMessage getResolvedTcmpMessage() {
        if(resolver == null) {
            return null;
        }
        else {
            if(resolvedTcmpMessage == null) {
                TCMPMessage rawTcmp = getRawTcmpMessage();
                try {
                    if(savedMessage.isFromMe()) {
                        resolvedTcmpMessage = resolver.parseCommand(rawTcmp);
                    }
                    else {
                        resolvedTcmpMessage = resolver.parseResponse(rawTcmp);
                    }
                } catch (MalformedPayloadException e) {
                    Timber.i(e, "Malformed payload");
                } catch (FamilyCodeNotSupportedException e) {
                    Timber.i(e, "Family code not supported");
                } catch (ResponseCodeNotSupportedException e) {
                    Timber.i(e,"Response code not supported");
                } catch (CommandCodeNotSupportedException e) {
                    Timber.i(e, "Command code not supported");
                }
            }
        }
        return resolvedTcmpMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParsedTcmpMessage that = (ParsedTcmpMessage) o;

        if(getSavedMessage().getDbId() != -1) {
            return getSavedMessage().getDbId().equals(that.getSavedMessage().getDbId());
        }
        else {
            return getSavedMessage().equals(that.getSavedMessage());
        }
    }

    @Override
    public int hashCode() {
        return getSavedMessage().hashCode();
    }
}
