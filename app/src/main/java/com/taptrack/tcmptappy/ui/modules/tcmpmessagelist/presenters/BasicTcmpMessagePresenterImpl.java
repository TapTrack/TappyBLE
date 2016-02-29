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

package com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.presenters;

import com.taptrack.tcmptappy.application.AppModule;
import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.domain.messagepersistence.TCMPMessagePersistenceService;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessageVista;
import com.taptrack.tcmptappy.ui.mvp.BasePresenter;
import com.taptrack.tcmptappy.utils.TimberSubscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import rx.Scheduler;
import rx.Subscription;

public class BasicTcmpMessagePresenterImpl
        extends BasePresenter<DisplayTcmpMessageVista> implements DisplayTcmpMessagePresenter {

    List<ParsedTcmpMessage> parsedTcmpMessages = new ArrayList<>();

    TCMPMessagePersistenceService messagePersistenceService;
    Subscription messagePersistenceSubscription;

    Scheduler uiScheduler;

    private static final int INITIAL_MAX = 30;
    private static final int LOAD_MORE_INCREMENT = 30;
    int maxAmount = INITIAL_MAX;

    public BasicTcmpMessagePresenterImpl(TCMPMessagePersistenceService messagePersistenceService,
                                         @Named(AppModule.NAME_SCHEDULER_UI) Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;

        attachTCMPMessagePersistenceService(messagePersistenceService);
    }

    protected void subscribeToTCMPMessagePersistenceService() {
        clearTCMPMessagePersistenceServiceSubscriptions();
        messagePersistenceSubscription = messagePersistenceService
                .getSortedLimitedThrottledResolvedTcmpMessages(maxAmount,200)
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<List<ParsedTcmpMessage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<ParsedTcmpMessage> parsedTcmpMessages) {
                        BasicTcmpMessagePresenterImpl.this.parsedTcmpMessages = parsedTcmpMessages;
                        redrawVista();
                    }
                });
    }

    @Override
    public void onNewVistaAttached() {
        redrawVista();
    }

    @Override
    public void redrawVista() {
        DisplayTcmpMessageVista vista = getVista();
        if(vista != null) {
            vista.displayMessages(parsedTcmpMessages);
        }
    }

    protected void attachTCMPMessagePersistenceService(TCMPMessagePersistenceService service) {
        clearTCMPMessagePersistenceServiceSubscriptions();
        messagePersistenceService = service;
        subscribeToTCMPMessagePersistenceService();
    }

    protected void detachTCMPMessagePersistenceService() {
        messagePersistenceService = null;
        clearTCMPMessagePersistenceServiceSubscriptions();
    }

    protected void clearTCMPMessagePersistenceServiceSubscriptions() {
        if (messagePersistenceSubscription != null && !messagePersistenceSubscription.isUnsubscribed()) {
            messagePersistenceSubscription.unsubscribe();
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

    @Override
    public void loadMore() {
        maxAmount += LOAD_MORE_INCREMENT;
        subscribeToTCMPMessagePersistenceService();
    }
}
