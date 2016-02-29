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

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PrettySheetScrollingBehavior extends AppBarLayout.ScrollingViewBehavior {
    public PrettySheetScrollingBehavior() {
        super();
    }

    public PrettySheetScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof PrettyCommandSheetView || super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if(dependency instanceof PrettyCommandSheetView) {
            ViewGroup.LayoutParams params = child.getLayoutParams();
            if(params instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams cllp = (CoordinatorLayout.LayoutParams) params;
                int visualSheetHeight = ((PrettyCommandSheetView) dependency).getVisualHeightMinusShadow();
                if(cllp.bottomMargin == visualSheetHeight) {
                    return false;
                }
                else {
                    cllp.bottomMargin = visualSheetHeight;
                    child.setLayoutParams(cllp);
                    return true;
                }
            }
            else {
                return false;
            }
        }
        else {
            return super.onDependentViewChanged(parent, child, dependency);
        }
    }

    //    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
//        if(dependency instanceof SelectTappyCommandSheetView) {
//            return updateOffset(parent,child,dependency);
//        }
//        else {
//            return super.onDependentViewChanged(parent, child, dependency);
//        }
//    }

//    protected boolean updateOffset (CoordinatorLayout parent, View child, View dependency) {
//        final CoordinatorLayout.Behavior behavior =
//                ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
//        if (behavior instanceof CoordinatorLayout.Behavior) {
//            // Offset the child so that it is below the app-bar (with any overlap)
//            final int offset = ((CoordinatorLayout.Behavior) behavior).getTopBottomOffsetForScrollingSibling();
//            setTopAndBottomOffset(dependency.getHeight() + offset
//                    - getOverlapForOffset(dependency, offset));
//            return true;
//        }
//        return false;
//    }
}
