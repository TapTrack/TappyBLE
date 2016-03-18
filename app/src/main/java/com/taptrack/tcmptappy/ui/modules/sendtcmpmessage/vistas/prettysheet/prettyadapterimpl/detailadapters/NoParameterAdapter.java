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
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DetailAdapterCommand;

public class NoParameterAdapter implements CommandDetailViewAdapter {
    public interface NoParameterCommand extends DetailAdapterCommand {
        TCMPMessage getMessage();
    }
    protected final NoParameterCommand command;

    public NoParameterAdapter(NoParameterCommand command) {
        this.command = command;
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent, Bundle bundle) {

    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent, Bundle bundle) {

    }

    @Override
    public int getTitleRes() {
        return command.getItem().getTitleRes();
    }

    @Override
    public int getDescriptionRes() {
        return command.getItem().getDescriptionRes();
    }

    @Override
    public void attachParameterView(ViewGroup parent) {
    }

    @Nullable
    @Override
    public TCMPMessage userDesiresSend(View parameterViewParent) {
        return command.getMessage();
    }
}
