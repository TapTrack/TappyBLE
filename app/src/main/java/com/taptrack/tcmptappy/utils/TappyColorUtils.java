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

package com.taptrack.tcmptappy.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;

public class TappyColorUtils {

    public static Drawable getTappyNameTintedDrawable(@NonNull Context ctx,
                                                   @NonNull TappyBleDeviceDefinition deviceDefinition,
                                                   @DrawableRes int drawable) {
        return DrawableTinter.getColorResTintedDrawable(
                ctx,
                drawable,
                getColorForTappy(deviceDefinition));
    }

    @ColorRes
    public static int getColorForTappy(@NonNull TappyBleDeviceDefinition deviceDefinition) {
        return getColorForTappy(deviceDefinition.getName(), deviceDefinition.getAddress());
    }

    @ColorRes
    public static int getColorForTappy(String name, String address) {
        if(name == null || name.length() == 0) {
            return getColorForTappy(6);
        }
        else {
            switch(name.charAt(name.length() - 1)) {
                case '0':
                    return getColorForTappy(0);
                case '1':
                    return getColorForTappy(1);
                case '2':
                    return getColorForTappy(2);
                case '3':
                    return getColorForTappy(3);
                case '4':
                    return getColorForTappy(4);
                case '5':
                    return getColorForTappy(5);
                default:
                    return getColorForTappy(name.hashCode());
            }
        }

    }

    @ColorRes
    private static int getColorForTappy(int ordinal) {
        if(ordinal < 0) {
            ordinal = ordinal * -1;
        }
        switch(ordinal % 7) {
            case 0:
                return R.color.tappy_color_1;
            case 1:
                return R.color.tappy_color_2;
            case 2:
                return R.color.tappy_color_3;
            case 3:
                return R.color.tappy_color_4;
            case 4:
                return R.color.tappy_color_5;
            case 5:
                return R.color.tappy_color_6;
            case 6:
            default:
                return R.color.tappy_color_7;

        }
    }
}
