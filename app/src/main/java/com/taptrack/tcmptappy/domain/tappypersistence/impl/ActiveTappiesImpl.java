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
import com.taptrack.tcmptappy.data.ActiveTappyDefinition;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.ActiveTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
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

public class ActiveTappiesImpl implements ActiveTappiesService {
    private final StorIOContentResolver resolver;
    private final Scheduler ioScheduler;

    @Inject
    public ActiveTappiesImpl(StorIOContentResolver contentResolver, Scheduler ioScheduler) {
        this.resolver = contentResolver;
        this.ioScheduler = ioScheduler;
    }

    public void activateTappy(TappyBleDeviceDefinition tappy) {
        resolver.put()
                .object(new ActiveTappyDefinition(tappy))
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
    public void inactivateTappy(TappyBleDeviceDefinition tappy) {
        resolver.delete()
                .object(new ActiveTappyDefinition(tappy))
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
    public Observable<Set<TappyBleDeviceDefinition>> getActiveTappies() {
        Query query = Query.builder()
                .uri(TappyBleDemoProvider.URI_ACTIVE)
                .columns(ActiveTappyPersistenceContract.ALL_PROJECTION)
                .build();
        PreparedGetListOfObjects<ActiveTappyDefinition> prep = resolver.get()
                .listOfObjects(ActiveTappyDefinition.class)
                .withQuery(query)
                .prepare();

//        return Observable.just((Set<TappyBleDeviceDefinition>) new HashSet<TappyBleDeviceDefinition>(0));
        return resolver
                .observeChangesOfUri(query.uri()) // each change triggers executeAsBlocking
                .map(MapSomethingToExecuteAsBlocking.newInstance(prep))
                .startWith(Observable.create(OnSubscribeExecuteAsBlocking.newInstance(prep))) // start stream with first query result
                .sample(100, TimeUnit.MILLISECONDS)
                .onBackpressureLatest() // this is questionable
                .subscribeOn(ioScheduler)
                .flatMap(new Func1<List<ActiveTappyDefinition>, Observable<List<ActiveTappyDefinition>>>() {
                    @Override
                    public Observable<List<ActiveTappyDefinition>> call(List<ActiveTappyDefinition> activeTappyDefinitions) {
                        return Observable.just(activeTappyDefinitions);
                    }
                })
                .map(new Func1<List<ActiveTappyDefinition>, Set<TappyBleDeviceDefinition>>() {
                    @Override
                    public Set<TappyBleDeviceDefinition> call(List<ActiveTappyDefinition> activeTappyDefinitions) {
                        return new HashSet<TappyBleDeviceDefinition>(activeTappyDefinitions);
                    }
                });
//        return resolver.get()
//                .listOfObjects(ActiveTappyDefinition.class)
//                .withQuery(Query.builder()
//                .uri(TappyBleDemoProvider.URI_ACTIVE)
//                .columns(ActiveTappyPersistenceContract.ALL_PROJECTION)
//                .build())
//                .prepare()
//                .asRxObservable()
//                .flatMap(new Func1<List<ActiveTappyDefinition>, Observable<List<ActiveTappyDefinition>>>() {
//                    @Override
//                    public Observable<List<ActiveTappyDefinition>> call(List<ActiveTappyDefinition> activeTappyDefinitions) {
//                        return Observable.just(activeTappyDefinitions);
//                    }
//                })
//                .subscribeOn(ioScheduler)
//                .map(new Func1<List<ActiveTappyDefinition>, Set<TappyBleDeviceDefinition>>() {
//                    @Override
//                    public Set<TappyBleDeviceDefinition> call(List<ActiveTappyDefinition> activeTappyDefinitions) {
//                        return new HashSet<TappyBleDeviceDefinition>(activeTappyDefinitions);
//                    }
//                });
    }
}
