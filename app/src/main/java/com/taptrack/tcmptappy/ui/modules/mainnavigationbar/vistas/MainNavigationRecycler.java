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

package com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.data.ActiveTappyWithStatus;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationPresenter;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationVista;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.ActiveTappyAdapterDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.CaptionAdaptorDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.HeadingAdapterDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.NavActionAdaptorDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.NavigationHeaderLayoutDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.SavedTappyAdapterDelegate;
import com.taptrack.tcmptappy.utils.TwoActionTappyListener;

import java.util.ArrayList;
import java.util.List;

public class MainNavigationRecycler extends RecyclerView implements MainNavigationVista{
    protected MainNavigationPresenter presenter;

    protected final TwoActionTappyListener activeTappyListener = new TwoActionTappyListener() {
        @Override
        public void onPrimaryAction(TappyBleDeviceDefinition tappyBle) {
            if(presenter != null)
                presenter.requestConnectTappy(tappyBle);
        }

        @Override
        public void onSecondaryAction(TappyBleDeviceDefinition tappyBle) {
            if(presenter != null)
                presenter.requestRemoveActiveTappy(tappyBle);
        }
    };

    protected final TwoActionTappyListener savedTappyListener = new TwoActionTappyListener() {
        @Override
        public void onPrimaryAction(TappyBleDeviceDefinition tappyBle) {
            if(presenter != null)
                presenter.requestSetActive(tappyBle);
        }

        @Override
        public void onSecondaryAction(TappyBleDeviceDefinition tappyBle) {
            if(presenter != null)
                presenter.requestRemoveSavedTappy(tappyBle);
        }
    };

    protected final NavActionListener navActionListener = new NavActionListener() {
        @Override
        public void findTappies() {
            if(presenter != null)
                presenter.requestSearchTappies();
        }

        @Override
        public void setCommunicationStatus(boolean isActive) {
            if(presenter != null)
                presenter.requestSetCommunication(isActive);
        }

        @Override
        public void setLaunchNdefUrl(boolean launchNdefUrl) {
            if(presenter != null)
                presenter.requestSetLaunchScannedUrl(launchNdefUrl);
        }

        @Override
        public void clearMessageDatabase() {
            if(presenter != null)
                presenter.requestClearMessageDatabase();
        }
    };

    protected List<WrappedNavItem> list = new ArrayList<>();

    protected List<ActiveTappyWithStatus> activeTappies = new ArrayList<>(0);
    protected List<TappyBleDeviceDefinition> savedTappies = new ArrayList<>(0);
    protected boolean isNdefLaunch = false;
    protected boolean isCommunicationActive = false;
    protected MainNavigationAdapter adapter;

    boolean showNavigationHeader = true;

    public MainNavigationRecycler(Context context) {
        super(context);
        init();
    }

    public MainNavigationRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainNavigationRecycler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        setLayoutManager(new LinearLayoutManager(getContext()));
//        TODO: create tablet layout
//        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//        if(dpWidth >= 600) {
//            showNavigationHeader = false;
//        }

        adapter = new MainNavigationAdapter(activeTappyListener,
                savedTappyListener,
                navActionListener);
        setAdapter(adapter);
    }

    @Override
    public void setActiveTappies(List<ActiveTappyWithStatus> tappies) {
        activeTappies = tappies;
        rebuildWrapper();
    }

    @Override
    public void setSavedTappies(List<TappyBleDeviceDefinition> tappies) {
        savedTappies = tappies;
        rebuildWrapper();
    }

    @Override
    public void setCommunicationActive(boolean isActive) {
        this.isCommunicationActive = isActive;
        rebuildWrapper();
    }

    @Override
    public void setNdefBackground(boolean isNdefBackground) {
        this.isNdefLaunch = isNdefBackground;
        rebuildWrapper();
    }

    @Override
    public void setDisplayData(List<ActiveTappyWithStatus> activeTappies,
                               List<TappyBleDeviceDefinition> savedTappies,
                               boolean communicationActive,
                               boolean ndefLaunch) {
        this.activeTappies = activeTappies;
        this.savedTappies = savedTappies;
        this.isCommunicationActive = communicationActive;
        this.isNdefLaunch = ndefLaunch;
        rebuildWrapper();
    }

    public void rebuildWrapper() {
        int wrapperSize = 4 +
                (activeTappies.size() >0 ? activeTappies.size() : 1) +
                (savedTappies.size() >0 ? savedTappies.size() : 1) +
                (showNavigationHeader ? 1:0);
        List<WrappedNavItem> wrappedNavItems = new ArrayList<>(wrapperSize);
        if(showNavigationHeader)
            wrappedNavItems.add(new NavigationHeaderLayoutDelegate.WrappedHeader());

        wrappedNavItems.add(new HeadingAdapterDelegate.WrappedHeading(R.string.active_tappies_heading));
        if(activeTappies.size() == 0) {
            wrappedNavItems.add(new CaptionAdaptorDelegate.WrappedCaption(R.string.no_active_tappies_caption));
        }
        else {
            for(ActiveTappyWithStatus deviceDefinition : activeTappies) {
                wrappedNavItems.add(new ActiveTappyAdapterDelegate.WrappedActiveTappyDefinition(deviceDefinition));
            }
        }

        wrappedNavItems.add(new HeadingAdapterDelegate.WrappedHeading(R.string.saved_tappies_list_heading));
        if(savedTappies.size() == 0) {
            wrappedNavItems.add(new CaptionAdaptorDelegate.WrappedCaption(R.string.no_saved_tappies_caption));
        }
        else {
            for(TappyBleDeviceDefinition deviceDefinition : savedTappies) {
                wrappedNavItems.add(new SavedTappyAdapterDelegate.WrappedTappyDefinition(deviceDefinition,false));
            }
        }

        wrappedNavItems.add(new HeadingAdapterDelegate.WrappedHeading(R.string.actions));
        wrappedNavItems.add(new NavActionAdaptorDelegate.WrappedNavActions(isCommunicationActive,isNdefLaunch));

        adapter.setItems(wrappedNavItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayTooManyTappies(int maxLimit) {

    }

    @Override
    public void registerPresenter(MainNavigationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unregisterPresenter() {
        this.presenter = null;
    }
}
