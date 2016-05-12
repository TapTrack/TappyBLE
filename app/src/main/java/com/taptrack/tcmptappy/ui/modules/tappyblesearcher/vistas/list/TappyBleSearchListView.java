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

package com.taptrack.tcmptappy.ui.modules.tappyblesearcher.vistas.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleDeviceSearchPresenter;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.TappyBleSearchListVista;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.WrappedTappyBle;
import com.taptrack.tcmptappy.utils.TappyBleDeviceSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TappyBleSearchListView extends FrameLayout implements TappyBleSearchListVista {
    @BindView(R.id.rv_recycler)
    RecyclerView recyclerView;

    boolean isScanning;

    List<WrappedTappyBle> tappies = new ArrayList<>();
    TappyBleSearchRecyclerAdapter adapter;

    TappyBleDeviceSearchPresenter tappyBleSearchPresenter;

    protected TappyBleDeviceSelectedListener tappySelectedListener = new TappyBleDeviceSelectedListener() {
        @Override
        public void onTappySelected(TappyBleDeviceDefinition tappy) {
            if(tappyBleSearchPresenter != null) {
                tappyBleSearchPresenter.onTappySelected(tappy);
            }
        }
    };

    public TappyBleSearchListView(Context context) {
        super(context);
        init(context);
    }

    public TappyBleSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TappyBleSearchListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TappyBleSearchListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    protected void init(Context ctx) {
        View v = inflate(getContext(), R.layout.search_tappy_view, this);

        ButterKnife.bind(this,v);
        adapter = new TappyBleSearchRecyclerAdapter();
        adapter.setHasStableIds(true);
        adapter.registerTappySelectedListener(tappySelectedListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);

        isScanning = false;

    }

    @Override
    public void setIsScanning(boolean isScanning) {
        this.isScanning = isScanning;
        updateScanView();
    }

    protected void updateScanView() {
        adapter.setShowLoading(this.isScanning);
    }

    @Override
    public void displayTappies(List<WrappedTappyBle> tappyList) {
        tappies = tappyList;
        adapter.replaceTappies(tappyList);
    }


    @Override
    public void registerPresenter(TappyBleDeviceSearchPresenter presenter) {
        tappyBleSearchPresenter = presenter;
    }

    @Override
    public void unregisterPresenter() {
        tappyBleSearchPresenter = null;
    }

}
