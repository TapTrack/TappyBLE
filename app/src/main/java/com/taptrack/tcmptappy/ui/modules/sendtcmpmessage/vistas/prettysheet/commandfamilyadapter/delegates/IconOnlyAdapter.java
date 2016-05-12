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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandfamilyadapter.delegates;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hannesdorfmann.adapterdelegates.AbsAdapterDelegate;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandFamilyItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandfamilyadapter.CommandFamilyAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconOnlyAdapter extends AbsAdapterDelegate<List<CommandFamilyItem>> {
    private final CommandFamilyAdapter.CommandFamilySelectedListener listener;
    private int selectedId;

    public IconOnlyAdapter(int viewType, CommandFamilyAdapter.CommandFamilySelectedListener listener, int selectedId) {
        super(viewType);
        this.listener = listener;
        this.selectedId = selectedId;
    }

    @Override
    public boolean isForViewType(@NonNull List<CommandFamilyItem> items, int position) {
        return items.get(position) != null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new CommandFamilyVH(inflater.inflate(R.layout.view_command_family_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull List<CommandFamilyItem> items, int position, @NonNull RecyclerView.ViewHolder holder) {
        CommandFamilyItem item = items.get(position);
        ((CommandFamilyVH) holder).configure(item);
    }

    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
    }

    protected class CommandFamilyVH extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_command_family)
        ImageView commandFamilyIcon;
        @BindView(R.id.v_command_family_underline)
        View underLineView;

        View itemView;

        private int familyIdentifier = Integer.MIN_VALUE;

        public CommandFamilyVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(familyIdentifier != Integer.MIN_VALUE)
                        listener.onCommandFamilySelected(familyIdentifier);
                }
            });
        }

        public void configure(CommandFamilyItem item) {
            Context ctx = commandFamilyIcon.getContext();
            familyIdentifier = item.getIdentifier();
            commandFamilyIcon.setImageDrawable(ContextCompat.getDrawable(ctx,item.getIconRes()));
            if(item.getIdentifier() == selectedId) {
                //could cache this color drawable but its probably nbd
                underLineView.setBackground(new ColorDrawable(ContextCompat.getColor(ctx,R.color.colorAccent)));
            }
            else {
                underLineView.setBackground(null);
            }
        }
    }
}
