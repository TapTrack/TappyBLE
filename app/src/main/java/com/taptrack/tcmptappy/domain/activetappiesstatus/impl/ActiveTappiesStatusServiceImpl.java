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

package com.taptrack.tcmptappy.domain.activetappiesstatus.impl;

import com.taptrack.tcmptappy.data.ActiveTappyWithStatus;
import com.taptrack.tcmptappy.domain.activetappiesstatus.ActiveTappiesStatusService;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyStatusService;
import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func2;

public class ActiveTappiesStatusServiceImpl implements ActiveTappiesStatusService {
    private final ActiveTappiesService activeTappiesService;
    private final TappyStatusService tappyStatusService;
    private final Scheduler ioScheduler;

    public ActiveTappiesStatusServiceImpl(ActiveTappiesService activeTappiesService,
                                          TappyStatusService tappyStatusService, Scheduler ioScheduler) {
        this.activeTappiesService = activeTappiesService;
        this.tappyStatusService = tappyStatusService;
        this.ioScheduler = ioScheduler;
    }

    @Override
    public Observable<Set<ActiveTappyWithStatus>> getActiveTappiesWithStatus() {
        Observable<Set<TappyBleDeviceDefinition>> devices =
                activeTappiesService.getActiveTappies();
        Observable<Map<String,Integer>> deviceStatuses =
                tappyStatusService.getStatusMap()
                        //throttling the tappy status service as this can be really rapid fire when connecting to multiple devices
                        .debounce(50, TimeUnit.MILLISECONDS);
        return Observable.combineLatest(devices, deviceStatuses, new CombineTappyStatus())
                .subscribeOn(ioScheduler)
                .onBackpressureLatest();
    }

    protected class CombineTappyStatus implements Func2<Set<TappyBleDeviceDefinition>,Map<String,Integer>,Set<ActiveTappyWithStatus>> {

        @Override
        public Set<ActiveTappyWithStatus> call(Set<TappyBleDeviceDefinition> tappyBleDeviceDefinitions, Map<String, Integer> stringIntegerMap) {
            Set<ActiveTappyWithStatus> newTappySet = new HashSet<>(tappyBleDeviceDefinitions.size());
            for(TappyBleDeviceDefinition deviceDefinition : tappyBleDeviceDefinitions) {
                Integer status = stringIntegerMap.get(deviceDefinition.getAddress());
                if(status == null)
                    status = TappyBleDeviceStatus.UNKNOWN;
                newTappySet.add(new ActiveTappyWithStatus(deviceDefinition, status));
            }
            return Collections.unmodifiableSet(newTappySet);
        }
    }

}
