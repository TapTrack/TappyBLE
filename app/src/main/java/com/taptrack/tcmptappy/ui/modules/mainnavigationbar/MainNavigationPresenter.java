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

package com.taptrack.tcmptappy.ui.modules.mainnavigationbar;

import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.ui.mvp.Presenter;

public interface MainNavigationPresenter extends Presenter<MainNavigationVista> {
    public void requestSetActive(TappyBleDeviceDefinition tappyBle);
    public void requestRemoveSavedTappy(TappyBleDeviceDefinition tappyBle);
    public void requestConnectTappy(TappyBleDeviceDefinition tappyBle);
    public void requestRemoveActiveTappy(TappyBleDeviceDefinition tappyBle);
    public void requestSearchTappies();
    public void requestSetCommunication(boolean isActive);
    public void requestSetLaunchScannedUrl(boolean launchUrls);
    public void requestClearMessageDatabase();

    public void attachContainer(MainNavigationContainer container);
    public void detachContainer();
}
