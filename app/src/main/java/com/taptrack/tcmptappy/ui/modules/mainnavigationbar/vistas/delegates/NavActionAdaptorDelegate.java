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

package com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.NavActionListener;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.WrappedNavItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavActionAdaptorDelegate extends AbsAdapterDelegate<List<WrappedNavItem>> {
    public static class WrappedNavActions implements WrappedNavItem {
        private final boolean communicationActive;
        private final boolean launchUrls;

        public WrappedNavActions(boolean communicationActive, boolean launchUrls) {
            this.communicationActive = communicationActive;
            this.launchUrls = launchUrls;
        }

        public boolean isCommunicationActive() {
            return communicationActive;
        }

        public boolean shouldLaunchUrls() {
            return launchUrls;
        }

        @Override
        public long getItemId() {
            return 4;
        }
    }

    protected final NavActionListener listener;

    public NavActionAdaptorDelegate(int viewType, NavActionListener navActionListener) {
        super(viewType);
        this.listener = navActionListener;
    }

    @Override
    public boolean isForViewType(List<WrappedNavItem> items, int position) {
        return items.get(position) instanceof WrappedNavActions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.listitem_navactions,parent,false);
        return new NavActionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<WrappedNavItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        WrappedNavActions actions = (WrappedNavActions) items.get(position);
        NavActionVH h = (NavActionVH) holder;
        h.configure(actions);
    }

    protected class NavActionVH extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_background_ndef)
        View backgroundNdefHolder;
        @BindView(R.id.cb_enable_ndef)
        CheckBox enableNdef;

        @BindView(R.id.rl_enable_comm)
        View enableCommHolder;
        @BindView(R.id.sw_enable_comm)
        SwitchCompat enableComm;

        @BindView(R.id.rl_find_tappies)
        View findTappiesHolder;

        @BindView(R.id.rl_clear_messages)
        View clearMessagesHolder;


        public NavActionVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            backgroundNdefHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableNdef.toggle();
                }
            });

            enableCommHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableComm.toggle();
                }
            });

            findTappiesHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.findTappies();
                }
            });

            enableComm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.setCommunicationStatus(isChecked);
                }
            });

            enableNdef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.setLaunchNdefUrl(isChecked);
                }
            });

            clearMessagesHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.clearMessageDatabase();
                }
            });
        }

        public void configure(WrappedNavActions actions) {
            if(enableComm.isChecked() != actions.isCommunicationActive())
                enableComm.setChecked(actions.isCommunicationActive());

            if(enableNdef.isChecked() != actions.shouldLaunchUrls())
                enableNdef.setChecked(actions.shouldLaunchUrls());
        }


    }
}
