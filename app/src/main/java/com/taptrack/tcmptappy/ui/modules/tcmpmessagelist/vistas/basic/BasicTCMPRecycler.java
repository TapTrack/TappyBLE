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

package com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.basic;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.PrettySheetScrollingBehavior;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessageVista;

import java.util.ArrayList;
import java.util.List;

@CoordinatorLayout.DefaultBehavior(PrettySheetScrollingBehavior.class)
public class BasicTCMPRecycler extends RecyclerView implements DisplayTcmpMessageVista{
    protected BasicTCMPAdapter adapter;
    protected List<ParsedTcmpMessage> displayingMessages = new ArrayList<>();
    protected DisplayTcmpMessagePresenter presenter;

    public BasicTCMPRecycler(Context context) {
        super(context);
        init();
    }

    public BasicTCMPRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasicTCMPRecycler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true));
        adapter = new BasicTCMPAdapter();
        setAdapter(adapter);
    }

    protected void updateAdapter() {
        adapter.setItems(displayingMessages);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayMessages(List<ParsedTcmpMessage> messages) {
        displayingMessages = messages;
        updateAdapter();
    }

    @Override
    public void prependMessages(List<ParsedTcmpMessage> messages) {
        List<ParsedTcmpMessage> newList = new ArrayList<>(messages.size()+displayingMessages.size());
        newList.addAll(messages);
        newList.addAll(displayingMessages);
        displayingMessages = newList;
        updateAdapter();
    }

    @Override
    public void appendMessages(List<ParsedTcmpMessage> messages) {
        List<ParsedTcmpMessage> newList = new ArrayList<>(messages.size()+displayingMessages.size());
        newList.addAll(displayingMessages);
        newList.addAll(messages);
        displayingMessages = newList;
        updateAdapter();
    }

    @Override
    public void registerPresenter(DisplayTcmpMessagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unregisterPresenter() {
        this.presenter = null;
    }
}
