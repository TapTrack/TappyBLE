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

public class CaptionAdaptorDelegate extends AbsAdapterDelegate<List<WrappedNavItem>> {
    public static class WrappedCaption implements WrappedNavItem {
        @StringRes
        final int captionRes;

        public WrappedCaption (@StringRes int captionRes) {
            this.captionRes = captionRes;
        }

        @StringRes
        public int getTextRes() {
            return captionRes;
        }

        @Override
        public long getItemId() {
            return captionRes;
        }
    }

    public CaptionAdaptorDelegate(int viewType) {
        super(viewType);
    }

    @Override
    public boolean isForViewType(List<WrappedNavItem> items, int position) {
        return items.get(position) instanceof WrappedCaption;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.listitem_navcaption,parent,false);
        return new CaptionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<WrappedNavItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        WrappedCaption caption = (WrappedCaption) items.get(position);
        CaptionVH h = (CaptionVH) holder;
        h.captionTxt.setText(caption.getTextRes());
    }

    protected static class CaptionVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_heading)
        TextView captionTxt;

        public CaptionVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
