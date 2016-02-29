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

package com.taptrack.tcmptappy.commlink;

import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;
import com.taptrack.tcmptappy.tcmp.common.CommandCodeNotSupportedException;
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy.tcmp.common.FamilyCodeNotSupportedException;
import com.taptrack.tcmptappy.tcmp.common.ResponseCodeNotSupportedException;

import java.util.concurrent.atomic.AtomicReference;

public class TcmpResolverListener implements TcmpMessageListener {
    private CommandFamilyMessageResolver resolver;

    private int mode;
    public static final int MODE_RECEIVED_MESSAGES = 0;
    public static final int MODE_SENT_MESSAGES = 1;

    private AtomicReference<TcmpMessageListener> resolvedListener = new AtomicReference<>();

    public TcmpResolverListener(CommandFamilyMessageResolver resolver, int mode) {
        if(resolver == null) {
            throw new NullPointerException("Cant use a null resolver!");
        }

        this.resolver = resolver;

        if(mode != MODE_RECEIVED_MESSAGES && mode != MODE_SENT_MESSAGES) {
            throw new IllegalArgumentException("mode must be one of MODE_RECEIVED_MESSAGES or MODE_SENT_MESSAGES");
        }

        this.mode = mode;

    }

    public void registerNextListener(TcmpMessageListener listener) {
        // this fail fast may seem stupid given the unregister listener does
        // this exact operation, but this is here to discourage people from
        // passing in null for that behaviour
        if(listener == null)
            throw new NullPointerException("Cant set a null resolver!");
        resolvedListener.set(listener);
    }

    public void unregisterNextListener() {
        resolvedListener.set(null);
    }

    @Override
    public void onNewTcmpMessage(com.taptrack.tcmptappy.tcmp.TCMPMessage tcmpMessage) {
        TcmpMessageListener target = resolvedListener.get();
        if(target != null) {
            if(mode == MODE_SENT_MESSAGES) {
                try {
                    target.onNewTcmpMessage(resolver.parseCommand(tcmpMessage));
                } catch (MalformedPayloadException | FamilyCodeNotSupportedException | CommandCodeNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    target.onNewTcmpMessage(resolver.parseResponse(tcmpMessage));
                } catch (MalformedPayloadException | FamilyCodeNotSupportedException | ResponseCodeNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
