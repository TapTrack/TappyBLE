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

package com.taptrack.tcmptappy.domain.tappypersistence;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.taptrack.tcmptappy.application.AppModule;
import com.taptrack.tcmptappy.dagger.ApplicationScope;
import com.taptrack.tcmptappy.domain.tappypersistence.impl.ActiveTappiesImpl;
import com.taptrack.tcmptappy.domain.tappypersistence.impl.SavedTappiesImpl;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class TappyPersistenceModule {
    @Provides
    @ApplicationScope
    SavedTappiesService getSavedTappyService(StorIOContentResolver resolver,
                                             @Named(AppModule.NAME_SCHEDULER_IO) Scheduler ioScheduler) {
//        return new MockSavedTappiesImpl(ioScheduler);
        return new SavedTappiesImpl(resolver,ioScheduler);
    }

    @Provides
    @ApplicationScope
    ActiveTappiesService getActiveTappyService(StorIOContentResolver resolver,
                                               @Named(AppModule.NAME_SCHEDULER_IO) Scheduler ioScheduler) {
//        return new MockActiveTappiesImpl(ioScheduler);
        return new ActiveTappiesImpl(resolver,ioScheduler);
    }
}
