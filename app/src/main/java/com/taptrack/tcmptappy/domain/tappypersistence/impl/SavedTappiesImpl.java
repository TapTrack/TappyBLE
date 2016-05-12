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
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.taptrack.tcmptappy.data.SavedTappyDefinition;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.SavedTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        Query query = Query.builder()
                .uri(TappyBleDemoProvider.URI_SAVED)
                .columns(SavedTappyPersistenceContract.ALL_PROJECTION)
                .build();
        PreparedGetListOfObjects<SavedTappyDefinition> prep = resolver.get()
                .listOfObjects(SavedTappyDefinition.class)
                .withQuery(query)
                .prepare();

        return resolver
                .observeChangesOfUri(query.uri()) // each change triggers executeAsBlocking
                .map(MapSomethingToExecuteAsBlocking.newInstance(prep))
                .startWith(Observable.create(OnSubscribeExecuteAsBlocking.newInstance(prep))) // start stream with first query result
                .sample(100, TimeUnit.MILLISECONDS)
                .onBackpressureLatest() // this is questionable
                .subscribeOn(ioScheduler)
                .map(new Func1<List<SavedTappyDefinition>, Set<TappyBleDeviceDefinition>>() {
                    @Override
                    public Set<TappyBleDeviceDefinition> call(List<SavedTappyDefinition> activeTappyDefinitions) {
                        return new HashSet<TappyBleDeviceDefinition>(activeTappyDefinitions);
                    }
                });
//                .subscribe(new Subscriber<Set<TappyBleDeviceDefinition>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e(e,"El roboto is confused");
//                    }
//
//                    @Override
//                    public void onNext(Set<TappyBleDeviceDefinition> savedTappyDefinitions) {
//
//                    }
//                });
//                .map(new Func1<List<SavedTappyDefinition>, Set<TappyBleDeviceDefinition>>() {
//                    @Override
//                    public Set<TappyBleDeviceDefinition> call(List<SavedTappyDefinition> activeTappyDefinitions) {
//                        return new HashSet<TappyBleDeviceDefinition>(activeTappyDefinitions);
//                    }
//                });
//        resolver.get()
//                .listOfObjects(SavedTappyDefinition.class)
//                .withQuery(query)
//                .prepare()
//                .asRxObservable()
//                .subscribe(new Subscriber<List<SavedTappyDefinition>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e(e,"Some stuff went down");
//                    }
//
//                    @Override
//                    public void onNext(List<SavedTappyDefinition> savedTappyDefinitions) {
//
//                    }
//                });
//        return Observable.just((Set<TappyBleDeviceDefinition>) new HashSet<TappyBleDeviceDefinition>());
//        return resolver.get()
//                .listOfObjects(SavedTappyDefinition.class)
//                .withQuery(Query.builder()
//                        .uri(TappyBleDemoProvider.URI_SAVED)
//                        .columns(SavedTappyPersistenceContract.ALL_PROJECTION)
//                        .build())
//                .prepare()
//                .asRxObservable()
//                .subscribeOn(ioScheduler)
//                .map(new Func1<List<SavedTappyDefinition>, Set<TappyBleDeviceDefinition>>() {
//                    @Override
//                    public Set<TappyBleDeviceDefinition> call(List<SavedTappyDefinition> activeTappyDefinitions) {
//                        return new HashSet<TappyBleDeviceDefinition>(activeTappyDefinitions);
//                    }
//                });
    }
}
