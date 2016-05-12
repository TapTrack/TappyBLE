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

package com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.vistas.basic.delegates;

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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestingParsedMessageAdapter extends AbsAdapterDelegate<List<ParsedTcmpMessage>> {
    public TestingParsedMessageAdapter(int viewType) {
        super(viewType);
    }

    @Override
    public boolean isForViewType(List<ParsedTcmpMessage> items, int position) {
        return true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.listitem_basic_tcmp_message,parent,false);
        return new TestingParsedMessageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull List<ParsedTcmpMessage> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        ((TestingParsedMessageVH) holder).configure(items.get(position));
    }

    protected static class TestingParsedMessageVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_source)
        TextView nameTxt;
        @BindView(R.id.tv_message)
        TextView msgType;

        View rootView;

        public TestingParsedMessageVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rootView = itemView;
        }

        public void configure(ParsedTcmpMessage message) {
            Context ctx = rootView.getContext();
            if(message.getSavedMessage().isFromMe()) {
                nameTxt.setText(R.string.me_sender);
                rootView.setBackgroundColor(ContextCompat.getColor(ctx,R.color.colorPrimary));
            }
            else {
                nameTxt.setText(message.getSavedMessage().getName());
                rootView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));
            }

            TCMPMessage resolved = message.getResolvedTcmpMessage();
            if(resolved == null)
                msgType.setText(R.string.unknown_message_type);
            else
                msgType.setText(resolved.getClass().getSimpleName());
        }
    }
}
