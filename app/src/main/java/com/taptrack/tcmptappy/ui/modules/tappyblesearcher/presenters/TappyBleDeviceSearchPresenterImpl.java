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

package com.taptrack.tcmptappy.ui.modules.tappyblesearcher.presenters;

import com.taptrack.tcmptappy.domain.devicesearch.TappyBleDeviceSearch;
import com.taptrack.tcmptappy.domain.tappypersistence.SavedTappiesService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleDeviceSearchPresenter;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleSearchListVista;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.WrappedTappyBle;
import com.taptrack.tcmptappy.ui.mvp.BasePresenter;
import com.taptrack.tcmptappy.utils.TimberSubscriber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;

public class TappyBleDeviceSearchPresenterImpl extends BasePresenter<TappyBleSearchListVista> implements TappyBleDeviceSearchPresenter {

    private TappyBleDeviceSearch searcher;
    private Subscription tappySubscriber;

    private SavedTappiesService savedTappiesService;
    private Subscription savedTappySubscription;

    private Map<String,TappyBleDeviceDefinition> foundTappyMap = new HashMap<>();
    private Map<String,TappyBleDeviceDefinition> savedTappyMap = new HashMap<>();

    private List<WrappedTappyBle> wrappedTappyBles = new ArrayList<>();

    private boolean desiredScanning = false;
    private boolean isScanning = false;

    private TappyBleSelectedListener listener;

    private final Scheduler uiScheduler;

    public TappyBleDeviceSearchPresenterImpl(TappyBleDeviceSearch deviceSearch, SavedTappiesService savedTappiesService, Scheduler uiScheduler) {
        this.uiScheduler = uiScheduler;

        this.searcher = deviceSearch;
        isScanning = deviceSearch.isScanning();

        this.savedTappiesService = savedTappiesService;


        subscribeToSearch();
        subscribeToSaved();
    }

    protected void subscribeToSearch() {
        unsubscribeFromSearch();
        tappySubscriber = searcher.getTappies()
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<TappyBleDeviceDefinition>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(TappyBleDeviceDefinition tappyBle) {
                        addTappy(tappyBle);
                    }
                });
    }

    protected void unsubscribeFromSearch() {
        if(tappySubscriber != null && !tappySubscriber.isUnsubscribed())
            tappySubscriber.unsubscribe();

        tappySubscriber = null;
    }

    protected void subscribeToSaved() {
        unsubscribeFromSaved();
        savedTappySubscription = savedTappiesService
                .getSavedTappies()
                .map(new ConvertSetToAddressMap())
                .observeOn(uiScheduler)
                .subscribe(new TimberSubscriber<Map<String,TappyBleDeviceDefinition>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Map<String,TappyBleDeviceDefinition> tappies) {
                        newSavedTappies(tappies);
                    }
                });
    }

    protected void unsubscribeFromSaved() {
        if(savedTappySubscription != null && !savedTappySubscription.isUnsubscribed())
            savedTappySubscription.unsubscribe();

        savedTappySubscription = null;
    }

    public void newSavedTappies(Map<String,TappyBleDeviceDefinition> tappies) {
        savedTappyMap = tappies;
        updateWrapper();
    }

    public void addTappy(TappyBleDeviceDefinition tappyBle) {
        if(! foundTappyMap.containsKey(tappyBle.getAddress())) {
            foundTappyMap.put(tappyBle.getAddress(), tappyBle);
            updateWrapper();
        }
    }

    public void updateWrapper() {
        List<WrappedTappyBle> newWrappedTappies = new ArrayList<>(foundTappyMap.size());
        Collection<TappyBleDeviceDefinition> foundTappies = foundTappyMap.values();
        for(TappyBleDeviceDefinition foundTappy : foundTappies) {
            //O(1) because hashset
            if(savedTappyMap.containsKey(foundTappy.getAddress())) {
                newWrappedTappies.add(new WrappedTappyBle(foundTappy,true));
            }
            else {
                newWrappedTappies.add(new WrappedTappyBle(foundTappy,false));
            }
        }
        wrappedTappyBles = newWrappedTappies;
        updateVista();
    }

    protected void updateVista() {
        TappyBleSearchListVista vista = getVista();
        if(vista != null) {
            vista.setIsScanning(desiredScanning);
            vista.displayTappies(new ArrayList<>(wrappedTappyBles));
        }
    }

    @Override
    public void setTappySelectedListener(TappyBleSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void unregisterTappySelectedListener() {
        this.listener = null;
    }

    @Override
    public void requestClearTappyList() {
        foundTappyMap.clear();
        updateVista();
    }

    protected void updateScanner() {
        if(desiredScanning) {
            searcher.startScanning();
        }
        else {
            searcher.stopScanning();
        }
        isScanning = searcher.isScanning();
        updateVista();
    }

    @Override
    public void requestStartScanning() {
        desiredScanning = true;
        updateScanner();
    }

    @Override
    public void requestStopScanning() {
        desiredScanning = false;
        updateScanner();
    }

    @Override
    public void onNewVistaAttached() {
        updateVista();
    }

    @Override
    public void redrawVista() {
        updateVista();
    }

    @Override
    public void onContextResume() {
        updateScanner();
    }

    @Override
    public void onContextPause() {
        searcher.stopScanning();
        isScanning = searcher.isScanning();
    }

    @Override
    public void onContextSaveState() {

    }

    @Override
    public void onTappySelected(TappyBleDeviceDefinition tappy) {
        if(!savedTappyMap.containsKey(tappy.getAddress()))
            savedTappiesService.saveTappy(tappy);
        else
            savedTappiesService.forgetTappy(tappy);
    }

    protected static class ConvertSetToAddressMap implements Func1<Set<TappyBleDeviceDefinition>,Map<String,TappyBleDeviceDefinition>> {

        @Override
        public Map<String, TappyBleDeviceDefinition> call(Set<TappyBleDeviceDefinition> tappyBleDeviceDefinitions) {
            Map<String,TappyBleDeviceDefinition> map = new HashMap<>(tappyBleDeviceDefinitions.size());
            for(TappyBleDeviceDefinition deviceDefinition : tappyBleDeviceDefinitions) {
                map.put(deviceDefinition.getAddress(),deviceDefinition);
            }
            return map;
        }
    }
}
