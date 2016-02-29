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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandadapter;

import com.hannesdorfmann.adapterdelegates.ListDelegationAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commandadapter.delegates.CommandItemAdapter;

import java.util.List;

public class CommandAdapter extends ListDelegationAdapter<List<CommandItem>> {
    public interface CommandSelectedListener {
        public void onCommandSelected(int commandType, int x, int y);
    }

    public CommandAdapter(CommandSelectedListener listener) {
        super();
        delegatesManager.addDelegate(new CommandItemAdapter(0,listener));
    }

    @Override
    public long getItemId(int position) {
        CommandItem item = getItems().get(position);
        if(item != null)
            return item.hashCode();
        else
            return super.getItemId(position);
    }
}
