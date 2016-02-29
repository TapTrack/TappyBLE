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

package com.taptrack.tcmptappy.domain.preferencepersistence;

import android.content.Context;

import com.taptrack.tcmptappy.application.AppModule;
import com.taptrack.tcmptappy.dagger.ApplicationScope;
import com.taptrack.tcmptappy.domain.preferencepersistence.impl.AppPreferenceServiceImpl;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class AppPreferenceModule {
    @Provides
    @ApplicationScope
    public AppPreferenceService getTappyDemoPreferenceService
            (@Named(AppModule.NAME_APP_CONTEXT) Context context,
             @Named(AppModule.NAME_SCHEDULER_IO) Scheduler ioScheduler) {
        return new AppPreferenceServiceImpl(context,ioScheduler);
    }
}
