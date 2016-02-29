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

public class CommandItem {
    private final int commandType;
    private final int titleRes;
    private final int descriptionRes;
    private final int iconRes;

    public CommandItem(int commandType, int titleRes, int descriptionRes, int iconRes) {
        this.commandType = commandType;
        this.titleRes = titleRes;
        this.descriptionRes = descriptionRes;
        this.iconRes = iconRes;
    }

    public int getCommandType() {
        return commandType;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public int getDescriptionRes() {
        return descriptionRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandItem that = (CommandItem) o;

        return getCommandType() == that.getCommandType();

    }

    @Override
    public int hashCode() {
        return getCommandType();
    }

}
