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

package com.taptrack.tcmptappy.domain.tappycommunication.impl;

import com.taptrack.tcmptappy.application.TappyManagerBinderProvider;
import com.taptrack.tcmptappy.domain.tappycommunication.TappyCommunicatorService;
import com.taptrack.tcmptappy.domain.tappycommunication.backgroundservices.TappyManagementBgService;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;

public class TappyCommunicatorServiceImpl implements TappyCommunicatorService {
    TappyManagerBinderProvider binderProvider;

    public TappyCommunicatorServiceImpl(TappyManagerBinderProvider binderProvider) {
        this.binderProvider = binderProvider;
    }

    @Override
    public void broadcastToAllTappies(TCMPMessage message) {
        TappyManagementBgService.TappyManagerBinder binder = binderProvider.getBinderOrNull();
        if(binder != null) {
            binder.broadcastMessage(message);
        }
    }


    @Override
    public void messageTappy(TappyBleDeviceDefinition deviceDefinition, TCMPMessage message) {
        TappyManagementBgService.TappyManagerBinder binder = binderProvider.getBinderOrNull();
        if(binder != null) {
            binder.messageTappy(deviceDefinition,message);
        }
    }

    @Override
    public void manualConnectTappy(TappyBleDeviceDefinition deviceDefinition) {
        TappyManagementBgService.TappyManagerBinder binder = binderProvider.getBinderOrNull();
        if(binder != null) {
            binder.connectTappy(deviceDefinition);
        }
    }
}
