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

package com.taptrack.tcmptappy.ui.modules.mainnavigationbar;

import com.taptrack.tcmptappy.application.AppModule;
import com.taptrack.tcmptappy.dagger.ApplicationScope;
import com.taptrack.tcmptappy.domain.activetappiesstatus.ActiveTappiesStatusService;
import com.taptrack.tcmptappy.domain.messagepersistence.TCMPMessagePersistenceService;
import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceService;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyCommunicatorService;
import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.presenters.MainNavigationPresenterImpl;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;

@Module
public class MainNavigationUiModule {
    @Provides
    @ApplicationScope
    MainNavigationPresenter getNavigationPresenter(
            ActiveTappiesStatusService tappyStatusServiceService,
            ActiveTappiesService activeTappiesService,
            SavedTappiesService savedTappiesService,
            AppPreferenceService appPreferenceService,
            TappyCommunicatorService tappyCommunicatorService,
            TCMPMessagePersistenceService messagePersistenceService,
            @Named(AppModule.NAME_SCHEDULER_UI) Scheduler uiScheduler) {
        return new MainNavigationPresenterImpl(
                tappyStatusServiceService,
                activeTappiesService,
                savedTappiesService,
                appPreferenceService,
                tappyCommunicatorService,
                messagePersistenceService,
                uiScheduler);
    }
}
