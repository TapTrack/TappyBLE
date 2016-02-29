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

package com.taptrack.tcmptappy.domain.tappycommunication.impl;

import com.taptrack.tcmptappy.domain.tappycommunication.TappyStatusService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class TappyStatusServiceImpl implements TappyStatusService {
    private interface TappyStatusListener {
        public void onUpdatedTappyStatusMap(Map<String,Integer> statusMap);
    }

    private final Scheduler ioScheduler;

    private static final Set<TappyStatusListener> listeners =
            new CopyOnWriteArraySet<>();
    private static final Map<String,Integer> statusMap =
            new HashMap<>();
    private static final ReadWriteLock statusRwLock = new ReentrantReadWriteLock();
    private static final Lock statusReadLock = statusRwLock.readLock();
    private static final Lock statusWriteLock = statusRwLock.writeLock();

    private Observable<Map<String,Integer>> tappyStatusObservable;

    public TappyStatusServiceImpl(Scheduler ioScheduler) {
        this.ioScheduler = ioScheduler;
        tappyStatusObservable = Observable.create(new Observable.OnSubscribe<Map<String,Integer>>() {

            @Override
            public void call(final Subscriber<? super Map<String,Integer>> subscriber) {
                final TappyStatusListener listener = new TappyStatusListener() {
                    public void onUpdatedTappyStatusMap(Map<String,Integer> statusMap) {
                        subscriber.onNext(statusMap);
                    }
                };

                TappyStatusServiceImpl.this.registerListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        TappyStatusServiceImpl.this.unregisterListener(listener);
                    }
                }));

                subscriber.onNext(getCurrentStatusMapSync());
            }
        }).subscribeOn(ioScheduler);
    }

    protected void registerListener(TappyStatusListener listener) {
        listeners.add(listener);
    }


    protected void unregisterListener(TappyStatusListener listener) {
        listeners.remove(listener);
    }

    protected void notifyStatusListeners() {
        Map<String,Integer> immutableMap = Collections.unmodifiableMap(getCurrentStatusMapSync());
        for(TappyStatusListener listener : listeners) {
            listener.onUpdatedTappyStatusMap(immutableMap);
        }
    }

    @Override
    public void publishNewStatusMap(Map<String, Integer> newStatusMap) {
        statusWriteLock.lock();
        statusMap.clear();
        statusMap.putAll(newStatusMap);
        statusWriteLock.unlock();
        notifyStatusListeners();
    }

    public Map<String,Integer> getCurrentStatusMapSync() {
        statusReadLock.lock();
        Map<String,Integer> newMap = new HashMap<>(statusMap);
        statusReadLock.unlock();
        return newMap;
    }

    @Override
    public Observable<Map<String, Integer>> getStatusMap() {
        return tappyStatusObservable
                .onBackpressureLatest()
                .subscribeOn(ioScheduler);
    }
}
