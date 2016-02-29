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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.WrappedNavItem;
import com.taptrack.tcmptappy.utils.TwoActionTappyListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SavedTappyAdapterDelegate extends AbsAdapterDelegate<List<WrappedNavItem>> {
    private final TwoActionTappyListener savedTappyListener;

    public static class WrappedTappyDefinition implements WrappedNavItem {
        private final TappyBleDeviceDefinition tappyBle;
        private final boolean isActive;

        public WrappedTappyDefinition(TappyBleDeviceDefinition tappyBle, boolean isActive) {
            this.tappyBle = tappyBle;
            this.isActive = isActive;
        }

        public String getName() {
            return tappyBle.getName();
        }

        public TappyBleDeviceDefinition getDeviceDefinition() {
            return tappyBle;
        }

        public boolean isActive() {
            return isActive;
        }

        @Override
        public long getItemId() {
            return tappyBle.hashCode() + (isActive ? 1231:0);
        }
    }

    public SavedTappyAdapterDelegate(int viewType,
                                     TwoActionTappyListener savedTappyListener) {
        super(viewType);
        this.savedTappyListener = savedTappyListener;
    }

    @Override
    public boolean isForViewType(List<WrappedNavItem> items, int position) {
        return items.get(position) instanceof WrappedTappyDefinition;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.listitem_tappy_secondary, parent, false);
        return new TappyVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<WrappedNavItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        WrappedTappyDefinition savedTappy = (WrappedTappyDefinition) items.get(position);
        TappyVH h = (TappyVH) holder;
        h.configure(savedTappy);
    }

    protected class TappyVH extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView nameTxt;
        @Bind(R.id.iv_icon)
        ImageView iconIv;
        @Bind(R.id.ib_secondary_action)
        ImageButton secondaryIb;

        View itemView;

        public TappyVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);

            Context ctx = itemView.getContext();
            iconIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_unarchive_black_24dp));
            secondaryIb.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_remove_circle_black_18dp));
        }

        public void configure(final WrappedTappyDefinition wrappy) {
            nameTxt.setText(wrappy.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savedTappyListener.onPrimaryAction(wrappy.getDeviceDefinition());
                }
            });

            secondaryIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savedTappyListener.onSecondaryAction(wrappy.getDeviceDefinition());
                }
            });

        }

    }
}
