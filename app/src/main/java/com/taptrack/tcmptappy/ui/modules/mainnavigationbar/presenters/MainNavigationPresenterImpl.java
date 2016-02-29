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

package com.taptrack.tcmptappy.ui.modules.mainnavigationbar.presenters;

import com.taptrack.tcmptappy.application.AppModule;
import com.taptrack.tcmptappy.data.ActiveTappyWithStatus;
import com.taptrack.tcmptappy.domain.activetappiesstatus.ActiveTappiesStatusService;
import com.taptrack.tcmptappy.domain.messagepersistence.TCMPMessagePersistenceService;
import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceService;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyCommunicatorService;
import com.taptrack.tcmptappy.domain.tappypersistence.ActiveTappiesService;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationContainer;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationPresenter;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationVista;
import com.taptrack.tcmptappy.ui.mvp.BasePresenter;
import com.taptrack.tcmptappy.utils.BleUtils;
import com.taptrack.tcmptappy.utils.TimberSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainNavigationPresenterImpl
        extends BasePresenter<MainNavigationVista> implements MainNavigationPresenter {

    private TCMPMessagePersistenceService messagePersistenceService;

    private ActiveTappiesStatusService activeTappiesStatusService;
    private Subscription activeTappyStatusSubscription;

    private SavedTappiesService savedTappiesService;
    private Subscription savedTappySubscription;

    private AppPreferenceService appPreferenceService;
    private CompositeSubscription preferenceSubscriptions;

    private TappyCommunicatorService tappyCommunicatorService;
    private ActiveTappiesService activeTappyService;

    private List<ActiveTappyWithStatus> activeTappies = new ArrayList<>();
    private List<TappyBleDeviceDefinition> savedTappies = new ArrayList<>();

    private Boolean activeCommunication = false;
    private Boolean launchNdef = false;

    private static final MainNavigationContainer DEFAULT = new MainNavigationContainer() {
        @Override
        public void displayTooManyTappies(int maxTappies) {

        }

        @Override
        public void requestStartTappySearch() {

        }
    };
    private MainNavigationContainer container = DEFAULT;

    private Scheduler uiScheduler;

    public MainNavigationPresenterImpl(ActiveTappiesStatusService tappyStatusServiceService,
                                       ActiveTappiesService activeTappiesService,
                                       SavedTappiesService savedTappiesService,
                                       AppPreferenceService preferenceService,
                                       TappyCommunicatorService tappyCommunicatorService,
                                       TCMPMessagePersistenceService messagePersistenceService,
                                        @Named(AppModule.NAME_SCHEDULER_UI) Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;

        this.messagePersistenceService = messagePersistenceService;
        this.tappyCommunicatorService = tappyCommunicatorService;
        this.activeTappyService = activeTappiesService;

        attachActiveTappyStatusService(tappyStatusServiceService);
        attachSavedTappiesService(savedTappiesService);
        attachAppPreferenceService(preferenceService);
    }


    public void attachContainer(MainNavigationContainer container) {
        this.container = container;
    }

    public void detachContainer() {
        this.container = DEFAULT;
    }

    protected void subscribeToSavedTappiesService() {
        savedTappySubscription = savedTappiesService.getSavedTappies()
                .map(new SortByDeviceName())
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<List<TappyBleDeviceDefinition>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<TappyBleDeviceDefinition> tappyBles) {
                        savedTappies = tappyBles;
                        redrawVista();
                    }
                });
    }


    protected void subscribeToActiveTappyStatusService() {
        activeTappyStatusSubscription = activeTappiesStatusService.getActiveTappiesWithStatus()
                .map(new SortByActiveDeviceByName())
                .subscribeOn(Schedulers.io())
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<List<ActiveTappyWithStatus>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<ActiveTappyWithStatus> tappyBles) {
                        activeTappies = tappyBles;
                        redrawVista();
                    }
                });
    }

    protected void subscribeToAppPreferenceService() {
        Subscription ndefSub = appPreferenceService
                .getBackgroundNdefLaunch()
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }


                    @Override
                    public void onNext(Boolean launchNdef) {
                        MainNavigationPresenterImpl.this.launchNdef = launchNdef;
                        redrawVista();
                    }
                });
        Subscription commSub = appPreferenceService
                .getCommunicationActive()
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Boolean communicationActive) {
                        MainNavigationPresenterImpl.this.activeCommunication = communicationActive;
                        redrawVista();
                    }
                });
        preferenceSubscriptions = new CompositeSubscription(ndefSub,commSub);
    }




    @Override
    public void requestSetActive(TappyBleDeviceDefinition tappyBle) {
        int maxTappies = BleUtils.getMaxBleCount();
        if(activeTappies.size() < maxTappies) {
            activeTappyService.activateTappy(tappyBle);
        }
        else {
            container.displayTooManyTappies(maxTappies);
            MainNavigationVista vista = getVista();
            if(vista != null) {
                vista.displayTooManyTappies(maxTappies);
            }
        }
    }

    @Override
    public void requestRemoveSavedTappy(TappyBleDeviceDefinition tappyBle) {
        savedTappiesService.forgetTappy(tappyBle);
    }

    @Override
    public void requestConnectTappy(TappyBleDeviceDefinition tappyBle) {
        tappyCommunicatorService.manualConnectTappy(tappyBle);
    }

    @Override
    public void requestRemoveActiveTappy(TappyBleDeviceDefinition tappyBle) {
        activeTappyService.inactivateTappy(tappyBle);
    }

    @Override
    public void requestSearchTappies() {
        if(this.container != null)
            container.requestStartTappySearch();
    }

    @Override
    public void requestSetCommunication(boolean isActive) {
        appPreferenceService.setCommunicationActive(isActive);
    }

    @Override
    public void requestSetLaunchScannedUrl(boolean launchUrls) {
        appPreferenceService.setNdefBackgroundLaunch(launchUrls);
    }

    @Override
    public void requestClearMessageDatabase() {
        messagePersistenceService.clearMessageDatabase();
    }

    @Override
    public void onNewVistaAttached() {
        redrawVista();
    }

    @Override
    public void redrawVista() {
        MainNavigationVista vista = getVista();
        if(vista != null) {
            vista.setDisplayData(activeTappies,
                    savedTappies,
                    activeCommunication,
                    launchNdef);
        }
    }

    @Override
    public void onContextResume() {

    }

    @Override
    public void onContextPause() {

    }

    @Override
    public void onContextSaveState() {

    }


    protected void clearSavedTappiesServiceSubscriptions() {
        if (savedTappySubscription != null && !savedTappySubscription.isUnsubscribed()) {
            savedTappySubscription.unsubscribe();
        }
    }

    protected void clearActiveTappyServiceSubscriptions() {
        if (activeTappyStatusSubscription != null && !activeTappyStatusSubscription.isUnsubscribed()) {
            activeTappyStatusSubscription.unsubscribe();
        }
    }

    protected void attachActiveTappyStatusService(ActiveTappiesStatusService service) {
        clearActiveTappyServiceSubscriptions();
        activeTappiesStatusService = service;
        subscribeToActiveTappyStatusService();
    }

    protected void detachActiveTappyService() {
        activeTappiesStatusService = null;
        clearActiveTappyServiceSubscriptions();
    }

    protected void attachSavedTappiesService(SavedTappiesService service) {
        clearSavedTappiesServiceSubscriptions();
        savedTappiesService = service;
        subscribeToSavedTappiesService();
    }

    protected void detachSavedTappiesService() {
        savedTappiesService = null;
        clearSavedTappiesServiceSubscriptions();
    }

    protected void clearAppPreferenceServiceSubscriptions() {
        if(preferenceSubscriptions != null && !preferenceSubscriptions.isUnsubscribed())
            preferenceSubscriptions.unsubscribe();
        preferenceSubscriptions = null;
    }

    protected void attachAppPreferenceService(AppPreferenceService service) {
        clearAppPreferenceServiceSubscriptions();
        appPreferenceService = service;
        subscribeToAppPreferenceService();
    }

    protected void detachAppPreferenceService() {
        appPreferenceService = null;
        clearAppPreferenceServiceSubscriptions();
    }

    private static class SortByActiveDeviceByName implements Func1<Set<ActiveTappyWithStatus>,List<ActiveTappyWithStatus>> {

        @Override
        public List<ActiveTappyWithStatus> call(Set<ActiveTappyWithStatus> tappyBles) {
            List<ActiveTappyWithStatus> tappyBleList = new ArrayList<ActiveTappyWithStatus>(tappyBles);
            Collections.sort(tappyBleList, new Comparator<ActiveTappyWithStatus>() {
                @Override
                public int compare(ActiveTappyWithStatus lhs, ActiveTappyWithStatus rhs) {
                    return lhs.getTappyDefinition().getName().compareTo(rhs.getTappyDefinition().getName());
                }
            });
            return tappyBleList;
        }
    }

    private static class SortByDeviceName implements Func1<Set<TappyBleDeviceDefinition>,List<TappyBleDeviceDefinition>> {

        @Override
        public List<TappyBleDeviceDefinition> call(Set<TappyBleDeviceDefinition> tappyBles) {
            List<TappyBleDeviceDefinition> tappyBleList = new ArrayList<TappyBleDeviceDefinition>(tappyBles);
            Collections.sort(tappyBleList, new Comparator<TappyBleDeviceDefinition>() {
                @Override
                public int compare(TappyBleDeviceDefinition lhs, TappyBleDeviceDefinition rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            return tappyBleList;
        }
    }
}
