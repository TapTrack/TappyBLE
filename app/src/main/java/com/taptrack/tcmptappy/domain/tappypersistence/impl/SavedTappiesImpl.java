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

package com.taptrack.tcmptappy.domain.tappypersistence.impl;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.taptrack.tcmptappy.data.SavedTappyDefinition;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.SavedTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;

public class SavedTappiesImpl implements SavedTappiesService {
    private final Scheduler ioScheduler;
    private final StorIOContentResolver resolver;

    @Inject
    public SavedTappiesImpl(StorIOContentResolver resolver, Scheduler ioScheduler) {
        this.resolver = resolver;
        this.ioScheduler = ioScheduler;
    }

    @Override
    public void saveTappy(TappyBleDeviceDefinition tappy) {
        resolver.put()
                .object(new SavedTappyDefinition(tappy))
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .subscribe(new Subscriber<PutResult>() {
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
    public void forgetTappy(TappyBleDeviceDefinition tappy) {
        resolver.delete()
                .object(new SavedTappyDefinition(tappy))
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .subscribe(new Subscriber<DeleteResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DeleteResult putResult) {
                        unsubscribe();
                    }
                });
    }

    @Override
    public Observable<Set<TappyBleDeviceDefinition>> getSavedTappies() {
        return resolver.get()
                .listOfObjects(SavedTappyDefinition.class)
                .withQuery(Query.builder()
                        .uri(TappyBleDemoProvider.URI_SAVED)
                        .columns(SavedTappyPersistenceContract.ALL_PROJECTION)
                        .build())
                .prepare()
                .asRxObservable()
                .subscribeOn(ioScheduler)
                .map(new Func1<List<SavedTappyDefinition>, Set<TappyBleDeviceDefinition>>() {
                    @Override
                    public Set<TappyBleDeviceDefinition> call(List<SavedTappyDefinition> activeTappyDefinitions) {
                        return new HashSet<TappyBleDeviceDefinition>(activeTappyDefinitions);
                    }
                });
    }
}
