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

package com.taptrack.tcmptappy.application;

import android.app.Application;
import android.content.Context;

import com.taptrack.tcmptappy.dagger.ApplicationScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class AppModule {
    public static final String NAME_APP_CONTEXT = "ApplicationContext";
    public static final String NAME_SCHEDULER_IO = "IO";
    public static final String NAME_SCHEDULER_UI = "UI";

    private static Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @ApplicationScope
    public TappyManagerBinderProvider provideTappyBinder() {
        return new TappyManagerBinderProvider((TcmpTappyDemo) application);
    }

    @Provides
    @ApplicationScope
    public Application getApplication() {
        return application;
    }

    @Provides
    @ApplicationScope
    @Named(AppModule.NAME_APP_CONTEXT)
    public Context provideAppContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Named(NAME_SCHEDULER_IO)
    public Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Named(NAME_SCHEDULER_UI)
    public Scheduler provideUiScheduler() {
        return AndroidSchedulers.mainThread();
    }

}
