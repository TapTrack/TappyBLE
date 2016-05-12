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
import com.taptrack.tcmptappy.data.ActiveTappyWithStatus;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceStatus;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.WrappedNavItem;
import com.taptrack.tcmptappy.utils.TappyColorUtils;
import com.taptrack.tcmptappy.utils.TwoActionTappyListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActiveTappyAdapterDelegate extends AbsAdapterDelegate<List<WrappedNavItem>> {
    private final TwoActionTappyListener activeTappyListener;

    public static class WrappedActiveTappyDefinition implements WrappedNavItem {
        private final ActiveTappyWithStatus tappyWithStatus;

        public WrappedActiveTappyDefinition(ActiveTappyWithStatus tappyWithStatus) {
            this.tappyWithStatus = tappyWithStatus;
        }

        public String getName() {
            return tappyWithStatus.getTappyDefinition().getName();
        }

        public TappyBleDeviceDefinition getDeviceDefinition() {
            return tappyWithStatus.getTappyDefinition();
        }

        public int getStatus() {
            return tappyWithStatus.getStatus();
        }

        @Override
        public long getItemId() {
            return tappyWithStatus.getTappyDefinition().hashCode() + 1231;
        }
    }

    public ActiveTappyAdapterDelegate(int viewType,
                                      TwoActionTappyListener activeTappyListener) {
        super(viewType);
        this.activeTappyListener = activeTappyListener;
    }

    @Override
    public boolean isForViewType(List<WrappedNavItem> items, int position) {
        return items.get(position) instanceof WrappedActiveTappyDefinition;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.listitem_tappy_secondary, parent, false);
        return new ActiveTappyVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<WrappedNavItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        WrappedActiveTappyDefinition savedTappy = (WrappedActiveTappyDefinition) items.get(position);
        ActiveTappyVH h = (ActiveTappyVH) holder;
        h.configure(savedTappy);
    }

    protected class ActiveTappyVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView nameTxt;
        @BindView(R.id.iv_icon)
        ImageView iconIv;
        @BindView(R.id.ib_secondary_action)
        ImageButton secondaryIb;

        View itemView;

        public ActiveTappyVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void configure(final WrappedActiveTappyDefinition wrappy) {
            Context ctx = itemView.getContext();
            int tappyStatus = wrappy.getStatus();

            TappyBleDeviceDefinition deviceDefinition = wrappy.getDeviceDefinition();
            if ((tappyStatus == TappyBleDeviceStatus.CONNECTING) ||
                    tappyStatus == TappyBleDeviceStatus.CONNECTED) {
                iconIv.setImageDrawable(
                        TappyColorUtils.getTappyNameTintedDrawable(
                                ctx,
                                deviceDefinition,
                                R.drawable.ic_cloud_black_24dp));
                iconIv.setAlpha(0.54f);
            } else if (tappyStatus == TappyBleDeviceStatus.READY) {
                iconIv.setImageDrawable(TappyColorUtils.getTappyNameTintedDrawable(
                        ctx,
                        deviceDefinition,
                        R.drawable.ic_cloud_done_white_24dp));
                iconIv.setAlpha(0.76f);
            } else if (tappyStatus == TappyBleDeviceStatus.ERROR) {
                iconIv.setImageDrawable(
                        TappyColorUtils.getTappyNameTintedDrawable(
                                ctx,
                                deviceDefinition,
                                R.drawable.ic_cloud_off_black_24dp));
                iconIv.setAlpha(0.76f);
            } else {
                iconIv.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_cloud_black_24dp));
                iconIv.setAlpha(0.54f);
            }

            secondaryIb.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_archive_black_18dp));
            nameTxt.setText(wrappy.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activeTappyListener.onPrimaryAction(wrappy.getDeviceDefinition());
                }
            });

            secondaryIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activeTappyListener.onSecondaryAction(wrappy.getDeviceDefinition());
                }
            });

        }

    }
}
