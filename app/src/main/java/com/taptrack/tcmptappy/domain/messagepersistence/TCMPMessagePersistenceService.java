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

package com.taptrack.tcmptappy.domain.messagepersistence;

import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;

import java.util.List;

import rx.Observable;

public interface TCMPMessagePersistenceService {
    public void saveTcmpMessage(SavedTcmpMessage message);
    public Observable<List<SavedTcmpMessage>> getSortedSavedTcmpMessages();
    public Observable<List<SavedTcmpMessage>> getSortedLimitedSavedTcmpMessages(int limit);
    public Observable<List<SavedTcmpMessage>> getSortedLimitedThrottledSavedTcmpMessages(int limit, int rate);

    public Observable<List<ParsedTcmpMessage>> getSortedResolvedTcmpMessages();
    public Observable<List<ParsedTcmpMessage>> getSortedLimitedResolvedTcmpMessages(int limit);
    public Observable<List<ParsedTcmpMessage>> getSortedLimitedThrottledResolvedTcmpMessages(int limit, int rate);
    public void clearMessageDatabase();
}
