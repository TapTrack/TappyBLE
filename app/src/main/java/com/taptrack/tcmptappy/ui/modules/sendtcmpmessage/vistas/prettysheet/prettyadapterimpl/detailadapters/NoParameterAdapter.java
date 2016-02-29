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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.GetBasicNfcLibraryVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StopCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetHardwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DefaultPrettySheetAdapter;

public class NoParameterAdapter implements CommandDetailViewAdapter {
    protected final CommandItem item;

    public NoParameterAdapter(CommandItem item) {
        this.item = item;
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent, Bundle bundle) {

    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent, Bundle bundle) {

    }

    @Override
    public int getTitleRes() {
        return item.getTitleRes();
    }

    @Override
    public int getDescriptionRes() {
        return item.getDescriptionRes();
    }

    @Override
    public void attachParameterView(ViewGroup parent) {
    }

    @Nullable
    @Override
    public TCMPMessage userDesiresSend(View parameterViewParent) {
        switch(item.getCommandType()) {
            case DefaultPrettySheetAdapter.KEY_SYS_LIBV:
                return new GetFirmwareVersionCommand();
            case DefaultPrettySheetAdapter.KEY_BATT:
                return new GetBatteryLevelCommand();
            case DefaultPrettySheetAdapter.KEY_HARDV:
                return new GetHardwareVersionCommand();
            case DefaultPrettySheetAdapter.KEY_PING:
                return new PingCommand();
            case DefaultPrettySheetAdapter.KEY_NFC_LIBV:
                return new GetBasicNfcLibraryVersionCommand();
            case DefaultPrettySheetAdapter.KEY_STOP:
                return new StopCommand();
        }
        return null;
    }
}
