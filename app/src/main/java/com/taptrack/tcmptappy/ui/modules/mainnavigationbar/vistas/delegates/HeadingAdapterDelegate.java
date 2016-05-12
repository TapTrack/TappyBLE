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
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.WrappedNavItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HeadingAdapterDelegate extends AbsAdapterDelegate<List<WrappedNavItem>> {
    public static class WrappedHeading implements WrappedNavItem {
        @StringRes
        final int headingRes;

        public WrappedHeading (@StringRes int headingRes) {
            this.headingRes = headingRes;
        }

        @StringRes
        public int getTextRes() {
            return headingRes;
        }

        @Override
        public long getItemId() {
            return headingRes;
        }
    }

    public HeadingAdapterDelegate(int viewType) {
        super(viewType);
    }

    @Override
    public boolean isForViewType(List<WrappedNavItem> items, int position) {
        return items.get(position) instanceof WrappedHeading;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.listitem_navheading,parent,false);
        return new HeadingVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<WrappedNavItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        WrappedHeading heading = (WrappedHeading) items.get(position);
        HeadingVH h = (HeadingVH) holder;
        h.captionTxt.setText(heading.getTextRes());
    }

    protected static class HeadingVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_heading)
        TextView captionTxt;

        public HeadingVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
