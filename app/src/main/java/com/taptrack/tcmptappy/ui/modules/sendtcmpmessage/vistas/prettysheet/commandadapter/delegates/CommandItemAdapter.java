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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandadapter.delegates;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandadapter.CommandAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommandItemAdapter extends AbsAdapterDelegate<List<CommandItem>> {
    private final CommandAdapter.CommandSelectedListener listener;

    public CommandItemAdapter(int viewType, CommandAdapter.CommandSelectedListener listener) {
        super(viewType);
        this.listener = listener;
    }

    @Override
    public boolean isForViewType(@NonNull List<CommandItem> items, int position) {
        return items.get(position) != null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new CommandVH(inflater.inflate(R.layout.view_big_icon_command_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<CommandItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        CommandItem item = items.get(position);
        ((CommandVH) holder).configure(item);
    }

    protected class CommandVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView titleText;
        @Nullable
        @BindView(R.id.tv_description)
        TextView descriptionTxt;
        @Nullable
        @BindView(R.id.iv_command_icon)
        ImageView commandIcon;

        View itemView;

        int currentCommandIdentitifer = -1;

        MotionEvent mostRecentUp;

        public CommandVH(final View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this,itemView);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        mostRecentUp = event;
                    }
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mostRecentUp != null) {
                        listener.onCommandSelected(currentCommandIdentitifer, (int) mostRecentUp.getRawX(), (int) mostRecentUp.getRawY());
                    }
                    else {
                        listener.onCommandSelected(currentCommandIdentitifer, (int) itemView.getX(), (int) itemView.getY());
                    }
                }
            });
            if (commandIcon != null) {
                commandIcon.setAlpha(0.54f);
            }
        }

        public void configure(CommandItem item) {
            Context ctx = itemView.getContext();
            currentCommandIdentitifer = item.getCommandType();
            titleText.setText(item.getTitleRes());
            if(descriptionTxt != null)
                descriptionTxt.setText(item.getDescriptionRes());
            if(commandIcon != null)
                commandIcon.setImageDrawable(ContextCompat.getDrawable(ctx,item.getIconRes()));
        }
    }
}
