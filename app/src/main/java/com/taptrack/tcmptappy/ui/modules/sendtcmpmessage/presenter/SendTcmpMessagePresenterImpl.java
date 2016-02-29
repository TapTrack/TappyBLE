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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.presenter;

import android.support.annotation.NonNull;

import com.taptrack.tcmptappy.domain.tappycommunication.TappyCommunicatorService;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessageVista;
import com.taptrack.tcmptappy.ui.mvp.BasePresenter;

import rx.Scheduler;

public class SendTcmpMessagePresenterImpl extends BasePresenter<SendTcmpMessageVista> implements SendTcmpMessagePresenter {
    private TappyCommunicatorService communicatorService;
    private Scheduler uiScheduler;

    private TCMPMessage lastMessage;

    public SendTcmpMessagePresenterImpl(TappyCommunicatorService communicatorService, Scheduler uiScheduler) {
        this.communicatorService = communicatorService;
        this.uiScheduler = uiScheduler;
    }

    @Override
    public void repeatLastMessage() {
        if(lastMessage != null)
            communicatorService.broadcastToAllTappies(lastMessage);
    }

    @Override
    public void sendTcmpMessage(@NonNull TCMPMessage message) {
        lastMessage = message;
        communicatorService.broadcastToAllTappies(message);
        SendTcmpMessageVista vista = getVista();
        if(vista != null)
            vista.enableRepeat(true);
    }

    @Override
    public void onNewVistaAttached() {
        redrawVista();
    }

    @Override
    public void redrawVista() {
        SendTcmpMessageVista vista = getVista();
        if(lastMessage != null && vista != null) {
            vista.enableRepeat(false);
        }
        else if (vista != null) {
            vista.disableRepeat(false);
        }
    }

}
