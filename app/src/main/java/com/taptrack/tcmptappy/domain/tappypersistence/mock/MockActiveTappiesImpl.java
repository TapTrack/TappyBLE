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

package com.taptrack.tcmptappy.domain.tappypersistence.mock;

import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class MockActiveTappiesImpl implements ActiveTappiesService {
    private interface ActiveTappiesListener {
        void onUpdatedList(Set<TappyBleDeviceDefinition> tappies);
    }
    private final Scheduler ioScheduler;

    private Set<TappyBleDeviceDefinition> tappyList = Collections.newSetFromMap(new ConcurrentHashMap<TappyBleDeviceDefinition, Boolean>());
    private Set<ActiveTappiesListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<ActiveTappiesListener, Boolean>());

    private Observable<Set<TappyBleDeviceDefinition>> tappyObs;

    public MockActiveTappiesImpl(Scheduler ioScheduler) {
        this.ioScheduler = ioScheduler;
        tappyObs = Observable.create(new Observable.OnSubscribe<Set<TappyBleDeviceDefinition>>() {

            @Override
            public void call(final Subscriber<? super Set<TappyBleDeviceDefinition>> subscriber) {
                final ActiveTappiesListener listener = new ActiveTappiesListener() {
                    @Override
                    public void onUpdatedList(Set<TappyBleDeviceDefinition> tappies) {
                        subscriber.onNext(tappies);
                    }
                };

                MockActiveTappiesImpl.this.registerListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        MockActiveTappiesImpl.this.unregisterListener(listener);
                    }
                }));

                subscriber.onNext(tappyList);
            }
        }).subscribeOn(ioScheduler);
    }

    @Override
    public void activateTappy(TappyBleDeviceDefinition tappy) {
        tappyList.add(tappy);
        notifyListeners();
    }

    @Override
    public void inactivateTappy(TappyBleDeviceDefinition tappy) {
        tappyList.remove(tappy);
        notifyListeners();
    }

    private void registerListener(ActiveTappiesListener listener) {
        listeners.add(listener);
    }

    private void unregisterListener(ActiveTappiesListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for(ActiveTappiesListener listener : listeners) {
            listener.onUpdatedList(tappyList);
        }
    }

    @Override
    public Observable<Set<TappyBleDeviceDefinition>> getActiveTappies() {
        return tappyObs.onBackpressureLatest().subscribeOn(ioScheduler);
    }
}
