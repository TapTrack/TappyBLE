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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.modules.tappyblesearcher.WrappedTappyBle;
import com.taptrack.tcmptappy.utils.TappyBleDeviceSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TappyBleSearchRecyclerAdapter extends RecyclerView.Adapter<TappyBleSearchRecyclerAdapter.TappyRecyclerHolder> {

    protected List<WrappedTappyBle> tappies;
    protected boolean showLoading;

    protected static final int VIEWTYPE_LOADER = 0;
    protected static final int VIEWTYPE_TAPPY = 1;

    protected TappyBleDeviceSelectedListener tappySelectedListener;

    protected static final TappyBleDeviceSelectedListener DEFAULT_LISTENER = new TappyBleDeviceSelectedListener() {
        @Override
        public void onTappySelected(TappyBleDeviceDefinition tappy) {

        }
    };

    public TappyBleSearchRecyclerAdapter() {
        tappies = new ArrayList<>();
        showLoading = false;
        tappySelectedListener = DEFAULT_LISTENER;
    }

    public void registerTappySelectedListener(TappyBleDeviceSelectedListener listener) {
        tappySelectedListener = listener;
    }

    public void unregisterTappySelectedListener() {
        tappySelectedListener = DEFAULT_LISTENER;
    }

    public void addTappy (WrappedTappyBle toAdd) {
        tappies.add(toAdd);
        notifyDataSetChanged();
    }

    public void addAllTappies(List<WrappedTappyBle> toAdd) {
        tappies.addAll(toAdd);
        notifyDataSetChanged();
    }

    public void replaceTappies(List<WrappedTappyBle> toReplace) {
        tappies.clear();
        tappies.addAll(toReplace);
        notifyDataSetChanged();
    }

    public void empty() {
        tappies.clear();
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        if(position < tappies.size()) {
            return tappies.get(position).hashCode();
        }
        else {
            return 0; //figuring this is crazy unlikely to collide
        }
    }

    @Override
    public TappyRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(VIEWTYPE_LOADER == viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.listitem_loader, parent, false);
            return new TappyLoadingIndicatorHolder(v);
        }
        else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.listitem_tappy, parent, false);
            return new TappyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(TappyRecyclerHolder holder, int position) {
        if(position < tappies.size()) {
            WrappedTappyBle tappy = tappies.get(position);
            ((TappyViewHolder) holder).loadTappy(tappy);
        }
    }

    @Override
    public int getItemCount() {
        if(showLoading)
            return tappies.size() + 1;
        else
            return tappies.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position >= tappies.size())
            return VIEWTYPE_LOADER;
        else
            return VIEWTYPE_TAPPY;
    }

    protected static class TappyRecyclerHolder extends RecyclerView.ViewHolder {
        public TappyRecyclerHolder(View itemView) {
            super(itemView);
        }
    }

    protected static class TappyLoadingIndicatorHolder extends TappyRecyclerHolder {

        public TappyLoadingIndicatorHolder(View itemView) {
            super(itemView);
        }
    }

    protected class TappyViewHolder extends TappyRecyclerHolder {
        @Bind(R.id.tv_name)
        TextView nameTextView;
        @Bind(R.id.iv_icon)
        ImageView icon;

        TappyBleDeviceDefinition displayedTappy;
        boolean isDisplayingSavedMode = false;

        public TappyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(displayedTappy != null)
                        tappySelectedListener.onTappySelected(displayedTappy);
                }
            });
        }

        public void loadTappy(@NonNull WrappedTappyBle tappy) {
            boolean updateDrawable = true;
            if(tappy.isSaved() == isDisplayingSavedMode)
                updateDrawable = false;

            displayedTappy = tappy.getTappyBleDevice();
            nameTextView.setText(displayedTappy.getName());

            isDisplayingSavedMode = tappy.isSaved();
            if(updateDrawable) {
                Context ctx = icon.getContext();
                if(isDisplayingSavedMode) {
                    icon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.ic_save_black_24dp));
                }
                else {
                    icon.setImageDrawable(ContextCompat.getDrawable(ctx,R.drawable.ic_bluetooth_searching_black_24dp));
                }
            }
        }
    }
}
