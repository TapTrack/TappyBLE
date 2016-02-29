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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;

public interface CommandDetailViewAdapter {

    public void storeTransientDataToBundle(ViewGroup parameterParent,Bundle bundle);
    public void restoreTransientDataFromBundle(ViewGroup parameterParent,Bundle bundle);

    @StringRes
    public int getTitleRes();
    @StringRes
    public int getDescriptionRes();

    /**
     * Attach additional parameter view
     *
     */
    public void attachParameterView(ViewGroup parent);

    /**
     * Call for when the user has clicked the 'Send' button
     *
     * Implementers should perform validation on the parameter
     * view's contents and display errors as necessary. Returns
     * the resulting TCMPMessage if verification proceeded
     * accordingly
     *
     * @param parameterViewParent
     * @return TCMP message to send, null if parameters are not valid
     */
    @Nullable
    public TCMPMessage userDesiresSend(View parameterViewParent);
}
