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

package com.taptrack.tcmptappy.domain.messagepersistence.rx;

import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ConvertToParsed implements Func1<List<SavedTcmpMessage>,List<ParsedTcmpMessage>> {
    private final CommandFamilyMessageResolver resolver;

    public ConvertToParsed(CommandFamilyMessageResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public List<ParsedTcmpMessage> call(List<SavedTcmpMessage> savedTcmpMessages) {
        List<ParsedTcmpMessage> parsedList = new ArrayList<>(savedTcmpMessages.size());
        for(SavedTcmpMessage msg : savedTcmpMessages) {
            ParsedTcmpMessage parsed = new ParsedTcmpMessage(msg);
            parsed.setCommandFamilyResolver(resolver);
            parsedList.add(parsed);
            parsed.getResolvedTcmpMessage(); // prompt lazy load here
        }
        return parsedList;
    }
}
