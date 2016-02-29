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
import com.taptrack.tcmptappy.domain.activetappiesstatus.ActiveTappiesStatusModule;
import com.taptrack.tcmptappy.domain.activetappiesstatus.ActiveTappiesStatusService;
import com.taptrack.tcmptappy.domain.contentprovider.ContentProviderModule;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.db.TappyBleDemoDbOpenHelper;
import com.taptrack.tcmptappy.domain.devicesearch.TappyBleDeviceSearch;
import com.taptrack.tcmptappy.domain.devicesearch.TappyBleDeviceSearchModule;
import com.taptrack.tcmptappy.domain.messagepersistence.MessagePersistenceModule;
import com.taptrack.tcmptappy.domain.messagepersistence.TCMPMessagePersistenceService;
import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceModule;
import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceService;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyCommunicationModule;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyStatusService;
import com.taptrack.tcmptappy.domain.tappycommunication.backgroundservices.TappyManagementBgService;
import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.domain.tappypersistence.TappyPersistenceModule;
import com.taptrack.tcmptappy.tcmp.common.CommandFamilyMessageResolver;
import com.taptrack.tcmptappy.ui.activities.interacttappy.InteractTappyActivityBollard;
import com.taptrack.tcmptappy.ui.activities.searchfortappies.SearchTappiesActivityBollard;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationPresenter;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationUiModule;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessageUiModule;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleDeviceSearchPresenter;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleSearchUiModule;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.TcmpMessageUiModule;

import javax.inject.Named;

import dagger.Component;

@ApplicationScope
@Component(modules = {
        AppModule.class,
        TappyBleDeviceSearchModule.class,
        TappyPersistenceModule.class,
        ContentProviderModule.class,
        MainNavigationUiModule.class,
        TappyBleSearchUiModule.class,
        AppPreferenceModule.class,
        TcmpMessageUiModule.class,
        MessagePersistenceModule.class,
        SendTcmpMessageUiModule.class,
        TappyCommunicationModule.class,
        ActiveTappiesStatusModule.class
})
public interface AppComponent {
    // appliation
    Application provideApplication();
    @Named(AppModule.NAME_APP_CONTEXT)
    Context provideAppContext();

    // domain helpers
    TappyBleDemoDbOpenHelper provideDbHelper();

    CommandFamilyMessageResolver getCommandFamilyMessageResolver();
    TappyManagerBinderProvider provideTappyManagerBinderProvider(); //dont ask

    // domain services
    TappyBleDeviceSearch getBleDeviceSearch();
    SavedTappiesService getSavedTappiesService();
    ActiveTappiesService getActiveTappiesService();
    AppPreferenceService getTappyDemoPreferenceService();
    TCMPMessagePersistenceService getMessagePersistenceService();
    ActiveTappiesStatusService getActiveTappyStatusService();
    TappyStatusService getTappyStatusService();

    // presenters
    TappyBleDeviceSearchPresenter getDeviceSearchPresenter();
    MainNavigationPresenter getNavigationPresenter();
    DisplayTcmpMessagePresenter getMessagePresenter();
    SendTcmpMessagePresenter getSendPresenter();

    // injection targets

    // system-level targets
    void inject(TappyBleDemoProvider provider);
    void inject(TappyManagementBgService service);

    // bollards
    void inject (SearchTappiesActivityBollard bollard);
    void inject(InteractTappyActivityBollard bollard);
}
