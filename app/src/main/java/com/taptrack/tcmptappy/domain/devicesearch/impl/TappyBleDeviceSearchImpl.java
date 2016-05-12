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

package com.taptrack.tcmptappy.domain.devicesearch.impl;

import com.taptrack.tcmptappy.dagger.ApplicationScope;
import com.taptrack.tcmptappy.domain.devicesearch.TappyBleDeviceSearch;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.scanner.TappyBleFoundListener;
import com.taptrack.tcmptappy.tappy.ble.scanner.TappyBleScanner;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

@ApplicationScope
public class TappyBleDeviceSearchImpl implements TappyBleDeviceSearch {
    private final TappyBleScanner scanner;
    private final Scheduler ioScheduler;

    boolean isScanning = false;

    private final Observable<TappyBleDeviceDefinition> tappyObs;

    @Inject
    public TappyBleDeviceSearchImpl(Scheduler ioScheduler) {
        this.ioScheduler = ioScheduler;
        scanner = TappyBleScanner.get();

        tappyObs = Observable.create(new Observable.OnSubscribe<TappyBleDeviceDefinition>() {

            @Override
            public void call(final Subscriber<? super TappyBleDeviceDefinition> subscriber) {
                final TappyBleFoundListener listener = new TappyBleFoundListener() {
                    @Override
                    public void onTappyBleFound(TappyBleDeviceDefinition tappyBle) {
                        subscriber.onNext(tappyBle);
                    }

                };

                scanner.registerTappyBleFoundListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        scanner.unregisterTappyBleFoundListener(listener);
                    }
                }));
            }
        }).subscribeOn(ioScheduler);
    }

    @Override
    public void startScanning() {
        isScanning = true;
        scanner.startScan();
    }

    @Override
    public void stopScanning() {
        isScanning = false;
        scanner.stopScan();
    }

    @Override
    public boolean isScanning() {
        return isScanning;
    }

    @Override
    public Observable<TappyBleDeviceDefinition> getTappies() {
        return tappyObs
                .onBackpressureLatest()
                .subscribeOn(ioScheduler);
    }
}
