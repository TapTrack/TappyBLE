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

package com.taptrack.tcmptappy.ui.activities.searchfortappies;

import android.os.Bundle;

import com.taptrack.tcmptappy.application.TcmpTappyDemo;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleDeviceSearchPresenter;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleSearchListVista;
import com.taptrack.tcmptappy.ui.mvp.BaseBollard;

import javax.inject.Inject;

public class SearchTappiesActivityBollard extends BaseBollard {
    @Inject
    TappyBleDeviceSearchPresenter tappyBleSearchPresenter;
    @Inject
    SavedTappiesService savedTappiesService;

    public SearchTappiesActivityBollard() {
        super();

//        DaggerSearchTappiesActivityComponent.builder()
//                .appComponent(TcmpTappyDemo.getAppComponent())
//                .build()
//                .inject(this);
        TcmpTappyDemo.getAppComponent()
                .inject(this);

        registerPresenterForCallbacks(tappyBleSearchPresenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    public void registerTappyListVista(TappyBleSearchListVista vista) {
        tappyBleSearchPresenter.registerVista(vista);
    }

    @Override
    public void onContextResume() {
        super.onContextResume();
        tappyBleSearchPresenter.requestStartScanning();
    }

    @Override
    public void onContextPause() {
        super.onContextPause();
        tappyBleSearchPresenter.requestStopScanning();
    }

    public void unregisterTappyListVista() {
        tappyBleSearchPresenter.unregisterVista();
    }
}
