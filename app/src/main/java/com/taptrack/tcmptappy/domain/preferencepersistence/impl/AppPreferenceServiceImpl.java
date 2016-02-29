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

package com.taptrack.tcmptappy.domain.preferencepersistence.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceService;

import rx.Observable;
import rx.Scheduler;

public class AppPreferenceServiceImpl implements AppPreferenceService {
    private final SharedPreferences preferences;
    private final RxSharedPreferences rxSharedPreferences;
    private final Scheduler ioScheduler;

    private static final String KEY_NDEF_LAUNCH = "NDEF_PREF";
    private static final String KEY_COMMUNICATION_ACTIVE = "COMM_PREF";

    public AppPreferenceServiceImpl(Context ctx, Scheduler ioScheduler) {
        this.ioScheduler = ioScheduler;
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        rxSharedPreferences = RxSharedPreferences.create(preferences);
    }

    @Override
    public void setCommunicationActive(boolean isActive) {
        preferences.edit().putBoolean(KEY_COMMUNICATION_ACTIVE,isActive).apply();
    }

    @Override
    public Observable<Boolean> getCommunicationActive() {
        return rxSharedPreferences
                .getBoolean(KEY_COMMUNICATION_ACTIVE,false)
                .asObservable()
                .subscribeOn(ioScheduler);
    }

    @Override
    public void setNdefBackgroundLaunch(boolean launchNdef) {
        preferences.edit().putBoolean(KEY_NDEF_LAUNCH,launchNdef).apply();
    }

    @Override
    public Observable<Boolean> getBackgroundNdefLaunch() {
        return rxSharedPreferences
                .getBoolean(KEY_NDEF_LAUNCH,false)
                .asObservable()
                .subscribeOn(ioScheduler);
    }
}
