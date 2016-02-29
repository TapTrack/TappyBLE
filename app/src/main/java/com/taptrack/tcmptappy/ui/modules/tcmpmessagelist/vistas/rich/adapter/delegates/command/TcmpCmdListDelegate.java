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

package com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich.adapter.delegates.command;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.rich.adapter.TcmpMessageSelectedListener;
import com.taptrack.tcmptappy.utils.TcmpMessageDescriptor;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TcmpCmdListDelegate extends AbsAdapterDelegate<List<ParsedTcmpMessage>> {
    private TcmpMessageSelectedListener listener;

    public TcmpCmdListDelegate(int viewType, TcmpMessageSelectedListener listener) {
        super(viewType);
        this.listener = listener;
    }


    @Override
    public boolean isForViewType(@NonNull List<ParsedTcmpMessage> items, int position) {
        ParsedTcmpMessage message = items.get(position);
        if(message.getSavedMessage().isFromMe()) {
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
        View v = inflater.inflate(R.layout.listitem_from_me,parent,false);
        return new NoParameterCmdViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<ParsedTcmpMessage> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        boolean isRepeated = false;
        if(position < (items.size() - 1))
            isRepeated = items.get(position + 1).getSavedMessage().isFromMe();

        ((NoParameterCmdViewHolder) holder).configure(items.get(position), isRepeated);
    }

    protected class NoParameterCmdViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_description)
        TextView descriptionText;

        @Bind(R.id.vg_data_holder)
        ViewGroup dataHolder;

        boolean isRepeatedMode = false;
        ParsedTcmpMessage currentMessage = null;

        View rootView;

        public NoParameterCmdViewHolder(View itemView) {
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
            if(isRepeated != isRepeatedMode) {
                if(isRepeated) {
                    dataHolder.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_bubble_me_sansserif));
                }
                else {
                    dataHolder.setBackground(ContextCompat.getDrawable(ctx,R.drawable.chat_bubble_me_serif));
                }
                isRepeatedMode = isRepeated;
            }

            TCMPMessage resolved = message.getResolvedTcmpMessage();
            descriptionText.setText(TcmpMessageDescriptor.getCommandDescription(resolved, ctx));
            currentMessage = message;
        }
    }
}
