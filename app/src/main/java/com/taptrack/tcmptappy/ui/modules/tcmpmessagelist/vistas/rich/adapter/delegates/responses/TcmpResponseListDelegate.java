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

package com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich.adapter.delegates.responses;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich.adapter.TcmpMessageSelectedListener;
import com.taptrack.tcmptappy.utils.DrawableTinter;
import com.taptrack.tcmptappy.utils.TappyColorUtils;
import com.taptrack.tcmptappy.utils.TcmpMessageDescriptor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TcmpResponseListDelegate extends AbsAdapterDelegate<List<ParsedTcmpMessage>> {
    private TcmpMessageSelectedListener listener;

    public TcmpResponseListDelegate(int viewType, TcmpMessageSelectedListener listener) {
        super(viewType);
        this.listener = listener;
    }


    @Override
    public boolean isForViewType(@NonNull List<ParsedTcmpMessage> items, int position) {
        ParsedTcmpMessage message = items.get(position);
        if(!message.getSavedMessage().isFromMe()) {
            return true;
        }
        else {
            return false;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.listitem_from_tappy,parent,false);
        return new NoParameterRespViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<ParsedTcmpMessage> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        boolean isRepeated = false;
        ParsedTcmpMessage item = items.get(position);
        if(position < (items.size() - 1)) {
            ParsedTcmpMessage lastMessage = items.get(position + 1);
            isRepeated = lastMessage.getSavedMessage().getAddress().equals(
                    item.getSavedMessage().getAddress());
        }

        ((NoParameterRespViewHolder) holder).configure(item, isRepeated);
    }

    protected class NoParameterRespViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_description)
        TextView descriptionText;

        @BindView(R.id.vg_data_holder)
        ViewGroup dataHolder;

        @BindView(R.id.vg_avatar_holder)
        ViewGroup tappyHolder;

        @BindView(R.id.iv_tappy_avatar)
        ImageView tappyAvatar;

        boolean isRepeatedMode = false;
        ParsedTcmpMessage currentMessage = null;

        View rootView;

        @ColorRes
        int avatarColor = -1;

        public NoParameterRespViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rootView = itemView;
            dataHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentMessage != null) {
                        listener.onTcmpDetailsRequested(currentMessage);
                    }
                }
            });
        }

        public void configure(ParsedTcmpMessage message, boolean isRepeated) {
            Context ctx = rootView.getContext();
            TCMPMessage resolved = message.getResolvedTcmpMessage();
            SavedTcmpMessage savedTcmpMessage = message.getSavedMessage();

            int color = TappyColorUtils.getColorForTappy(savedTcmpMessage.getName(),
                    savedTcmpMessage.getAddress());
            if(color != avatarColor) {
                tappyHolder.setBackground(DrawableTinter.getColorResTintedDrawable(ctx, R.drawable.dr_circle, color));
                avatarColor = color;
            }

            if(isRepeated != isRepeatedMode) {
                if(isRepeated) {
                    tappyHolder.getLayoutParams().height = 0;
                    tappyHolder.setVisibility(View.INVISIBLE);
                    dataHolder.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_bubble_tappy_sansserif));
                }
                else {
                    tappyHolder.getLayoutParams().height = ctx.getResources().getDimensionPixelSize(R.dimen.tappy_avatar_size);
                    tappyHolder.setVisibility(View.VISIBLE);
                    dataHolder.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_bubble_tappy_serif));
                }
                isRepeatedMode = isRepeated;
            }

            descriptionText.setText(TcmpMessageDescriptor.getResponseDescription(resolved, ctx));
            currentMessage = message;
        }
    }
}
