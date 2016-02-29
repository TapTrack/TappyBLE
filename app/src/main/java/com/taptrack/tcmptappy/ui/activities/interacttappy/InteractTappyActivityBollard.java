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

package com.taptrack.tcmptappy.ui.activities.interacttappy;

import android.content.Context;
import android.os.Bundle;

import com.taptrack.tcmptappy.application.TcmpTappyDemo;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationContainer;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationPresenter;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationVista;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessageVista;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleDeviceSearchPresenter;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleSearchListVista;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessageVista;
import com.taptrack.tcmptappy.ui.mvp.BaseBollard;

import javax.inject.Inject;

public class InteractTappyActivityBollard extends BaseBollard {
    @Inject
    TappyBleDeviceSearchPresenter tappyBleSearchPresenter;
    @Inject
    MainNavigationPresenter mainNavigationPresenter;
    @Inject
    DisplayTcmpMessagePresenter displayTcmpMessagePresenter;
    @Inject
    SendTcmpMessagePresenter sendTcmpMessagePresenter;

    public InteractTappyActivityBollard() {
        super();

//        DaggerInteractTappyActivityComponent.builder()
//                .appComponent(TcmpTappyDemo.getAppComponent())
//                .build()
//                .inject(this);
        TcmpTappyDemo.getAppComponent()
                .inject(this);

        registerPresenterForCallbacks(mainNavigationPresenter);
        registerPresenterForCallbacks(tappyBleSearchPresenter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MainNavigationContainer)
            mainNavigationPresenter.attachContainer((MainNavigationContainer) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainNavigationPresenter.detachContainer();
    }


    public void attachTcmpMessageVista(DisplayTcmpMessageVista tcmpVista) {
        displayTcmpMessagePresenter.registerVista(tcmpVista);
    }

    public void detachTcmpMessageVista() {
        displayTcmpMessagePresenter.unregisterVista();
    }

    public void attachMainNavigationVista(MainNavigationVista navigationVista) {
        mainNavigationPresenter.registerVista(navigationVista);
    }

    public void detachMainNavigationVista() {
        mainNavigationPresenter.unregisterVista();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    public void attachSendTcmpMessageVista(SendTcmpMessageVista sendVista) {
        sendTcmpMessagePresenter.registerVista(sendVista);
    }

    public void detachSendTcmpMessageVista() {
        sendTcmpMessagePresenter.unregisterVista();
    }

    public void attachTappyBleSearchListVista(TappyBleSearchListVista vista) {
        tappyBleSearchPresenter.registerVista(vista);
    }

    public void detachTappyBleSearchListVista() {
        tappyBleSearchPresenter.unregisterVista();
    }

}
