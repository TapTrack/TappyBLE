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

package com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.PrettySheetScrollingBehavior;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessageVista;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich.adapter.TcmpMessageAdapter;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich.adapter.TcmpMessageSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@CoordinatorLayout.DefaultBehavior(PrettySheetScrollingBehavior.class)
public class RichTcmpMessageVista extends SwipeRefreshLayout implements DisplayTcmpMessageVista {
    @Bind(R.id.rv_message_list)
    RecyclerView messageList;

    private DisplayTcmpMessagePresenter presenter;

    private final TcmpMessageSelectedListener messageSelectedListener = new TcmpMessageSelectedListener() {
        @Override
        public void onTcmpDetailsRequested(ParsedTcmpMessage message) {

        }
    };

    private final OnRefreshListener refreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            if(presenter != null) {
                presenter.loadMore();
            }
        }
    };

    private TcmpMessageAdapter adapter;

    public RichTcmpMessageVista(Context context) {
        super(context);
        init();
    }

    public RichTcmpMessageVista(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Context ctx = getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.view_rich_tcmp,this,true);
        ButterKnife.bind(this,v);

        adapter = new TcmpMessageAdapter(messageSelectedListener);
        adapter.setHasStableIds(true);
        LinearLayoutManager manager = new LinearLayoutManager(ctx);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        messageList.setLayoutManager(manager);
        messageList.setAdapter(adapter);

        this.setOnRefreshListener(refreshListener);
    }

    @Override
    public void displayMessages(List<ParsedTcmpMessage> messages) {
        setRefreshing(false);
        adapter.setItems(messages);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void prependMessages(List<ParsedTcmpMessage> messages) {
        setRefreshing(false);
        List<ParsedTcmpMessage> currentItems = adapter.getItems();
        if(currentItems.size() == 0) {
            displayMessages(messages);
        }
        else {
            List<ParsedTcmpMessage> newList = new ArrayList<>(currentItems.size() + messages.size());
            newList.addAll(messages);
            newList.addAll(currentItems);
            adapter.setItems(newList);
            adapter.notifyItemRangeInserted(0, messages.size());
        }
    }

    @Override
    public void appendMessages(List<ParsedTcmpMessage> messages) {
        setRefreshing(false);
        List<ParsedTcmpMessage> currentItems = adapter.getItems();
        if(currentItems.size() == 0) {
            displayMessages(messages);
        }
        else {
            List<ParsedTcmpMessage> newList = new ArrayList<>(currentItems.size() + messages.size());
            newList.addAll(currentItems);
            newList.addAll(messages);
            adapter.setItems(newList);
            adapter.notifyItemRangeInserted(currentItems.size(), messages.size());
        }
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
