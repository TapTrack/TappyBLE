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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class CommandFamilyItem {
    @DrawableRes
    private final int iconRes;
    @StringRes
    private final int descriptionRes;

    private final int identifier;

    public CommandFamilyItem(int iconRes, int descriptionRes, int identifier) {
        this.iconRes = iconRes;
        this.descriptionRes = descriptionRes;
        this.identifier = identifier;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getDescriptionRes() {
        return descriptionRes;
    }

    public int getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandFamilyItem that = (CommandFamilyItem) o;

        return getIdentifier() == that.getIdentifier();

    }

    @Override
    public int hashCode() {
        return getIdentifier();
    }
}
