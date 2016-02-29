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

package com.taptrack.tcmptappy.domain.messagepersistence.impl;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.TcmpMessagePersistenceContract;
import com.taptrack.tcmptappy.domain.messagepersistence.TCMPMessagePersistenceService;
import com.taptrack.tcmptappy.domain.messagepersistence.rx.ConvertToParsed;
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy.utils.TimberSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;

public class TCMPMessagePersistenceServiceImpl implements TCMPMessagePersistenceService {
    private final StorIOContentResolver contentResolver;
    private final CommandFamilyMessageResolver messageResolver;
    private final Scheduler ioScheduler;

    public TCMPMessagePersistenceServiceImpl(StorIOContentResolver contentResolver,
                                             CommandFamilyMessageResolver messageResolver,
                                             Scheduler ioScheduler) {
        this.contentResolver = contentResolver;
        this.messageResolver = messageResolver;
        this.ioScheduler = ioScheduler;
    }

    @Override
    public void saveTcmpMessage(SavedTcmpMessage message) {
        contentResolver.put()
                .object(message)
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .observeOn(ioScheduler)
                .subscribe(new TimberSubscriber<PutResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PutResult putResult) {
                        unsubscribe();
                    }
                });
    }

    @Override
    public Observable<List<SavedTcmpMessage>> getSortedSavedTcmpMessages() {
        return contentResolver.get()
                .listOfObjects(SavedTcmpMessage.class)
                .withQuery(Query.builder()
                        .uri(TappyBleDemoProvider.URI_TCMP)
                        .columns(TcmpMessagePersistenceContract.ALL_PROJECTION)
                        .sortOrder(TcmpMessagePersistenceContract.TIMESTAMP+" ASC").build())
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler);
    }

    @Override
    public Observable<List<SavedTcmpMessage>> getSortedLimitedSavedTcmpMessages(int limit) {
        return contentResolver.get()
                .listOfObjects(SavedTcmpMessage.class)
                .withQuery(Query.builder()
                        .uri(TappyBleDemoProvider.URI_TCMP)
                        .columns(TcmpMessagePersistenceContract.ALL_PROJECTION)
                        .sortOrder(TcmpMessagePersistenceContract.TIMESTAMP + " DESC")
                        .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .map(new LimitList(limit));
    }

    @Override
    public Observable<List<SavedTcmpMessage>> getSortedLimitedThrottledSavedTcmpMessages(int limit, int samplePeriod) {
        return contentResolver.get()
                .listOfObjects(SavedTcmpMessage.class)
                .withQuery(Query.builder()
                        .uri(TappyBleDemoProvider.URI_TCMP)
                        .columns(TcmpMessagePersistenceContract.ALL_PROJECTION)
                        .sortOrder(TcmpMessagePersistenceContract.TIMESTAMP + " DESC")
                        .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .sample(samplePeriod, TimeUnit.MILLISECONDS)
                .map(new LimitList(limit));
    }

    @Override
    public Observable<List<ParsedTcmpMessage>> getSortedResolvedTcmpMessages() {
        return contentResolver.get()
                .listOfObjects(SavedTcmpMessage.class)
                .withQuery(Query.builder()
                        .uri(TappyBleDemoProvider.URI_TCMP)
                        .columns(TcmpMessagePersistenceContract.ALL_PROJECTION)
                        .sortOrder(TcmpMessagePersistenceContract.TIMESTAMP + " DESC").build())
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .map(new ConvertToParsed(messageResolver));
    }

    @Override
    public Observable<List<ParsedTcmpMessage>> getSortedLimitedResolvedTcmpMessages(int limit) {
        return getSortedLimitedSavedTcmpMessages(limit)
                .map(new ConvertToParsed(messageResolver));
    }

    @Override
    public Observable<List<ParsedTcmpMessage>> getSortedLimitedThrottledResolvedTcmpMessages(int limit, int samplePeriod) {
        return getSortedLimitedThrottledSavedTcmpMessages(limit,samplePeriod)
                .map(new ConvertToParsed(messageResolver));
    }

    @Override
    public void clearMessageDatabase() {
        contentResolver.delete()
                .byQuery(DeleteQuery.builder()
                        .uri(TappyBleDemoProvider.URI_TCMP)
                        .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .observeOn(ioScheduler)
                .subscribe(new TimberSubscriber<DeleteResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(DeleteResult deleteResult) {
                        unsubscribe();
                    }
                });
    }

    protected class LimitList implements Func1<List<SavedTcmpMessage>,List<SavedTcmpMessage>> {
        private int maxLimit;

        public LimitList(int limit) {
            this.maxLimit = limit;
        }

        @Override
        public List<SavedTcmpMessage> call(List<SavedTcmpMessage> savedTcmpMessages) {
            if(savedTcmpMessages.size() < maxLimit) {
                return savedTcmpMessages;
            }
            else {
                List<SavedTcmpMessage> limitedList = new ArrayList<>(maxLimit);
                for(int i = 0; i < maxLimit; i++) {
                    limitedList.add(savedTcmpMessages.get(i));
                }
                return limitedList;
            }
        }
    }

}
