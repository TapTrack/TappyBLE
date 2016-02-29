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

package com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas;

import com.hannesdorfmann.adapterdelegates.ListDelegationAdapter;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.ActiveTappyAdapterDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.CaptionAdaptorDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.HeadingAdapterDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.NavActionAdaptorDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.NavigationHeaderLayoutDelegate;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.vistas.delegates.SavedTappyAdapterDelegate;
import com.taptrack.tcmptappy.utils.TwoActionTappyListener;

import java.util.List;

public class MainNavigationAdapter extends ListDelegationAdapter<List<WrappedNavItem>> {
    public MainNavigationAdapter(TwoActionTappyListener activeTappyListener,
                                 TwoActionTappyListener savedTappyListener,
                                 NavActionListener navActionListener) {
        super();
        delegatesManager.addDelegate(new SavedTappyAdapterDelegate(0, savedTappyListener));
        delegatesManager.addDelegate(new HeadingAdapterDelegate(1));
        delegatesManager.addDelegate(new CaptionAdaptorDelegate(2));
        delegatesManager.addDelegate(new NavActionAdaptorDelegate(3, navActionListener));
        delegatesManager.addDelegate(new NavigationHeaderLayoutDelegate(4));
        delegatesManager.addDelegate(new ActiveTappyAdapterDelegate(5,activeTappyListener));

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItems().get(position).getItemId();
    }
}
