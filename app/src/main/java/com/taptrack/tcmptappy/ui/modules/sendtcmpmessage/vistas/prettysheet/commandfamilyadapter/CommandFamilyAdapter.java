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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandfamilyadapter;

import com.hannesdorfmann.adapterdelegates.ListDelegationAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandFamilyItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandfamilyadapter.delegates.IconOnlyAdapter;

import java.util.List;

public class CommandFamilyAdapter extends ListDelegationAdapter<List<CommandFamilyItem>> {
    IconOnlyAdapter adapterDelegate;
    public int selectedId = -1;

    public interface CommandFamilySelectedListener {
        public void onCommandFamilySelected(int identifier);
    }

    public CommandFamilyAdapter(CommandFamilySelectedListener listener) {
        super();
        adapterDelegate = new IconOnlyAdapter(0,listener, -1);
        delegatesManager.addDelegate(adapterDelegate);
    }

    public void setSelectedId(int id) {
        adapterDelegate.setSelectedId(id);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        CommandFamilyItem item = getItems().get(position);
        if(item != null)
            return item.hashCode();
        else
            return super.getItemId(position);
    }
}
