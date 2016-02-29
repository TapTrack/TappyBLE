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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.fab;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessagePresenter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessageVista;

public class RepeatLastMessageFab extends FloatingActionButton implements SendTcmpMessageVista {
    SendTcmpMessagePresenter presenter;

    public RepeatLastMessageFab(Context context) {
        super(context);
        init();
    }

    public RepeatLastMessageFab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepeatLastMessageFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presenter != null)
                    presenter.repeatLastMessage();
            }
        });
    }

    @Override
    public void registerPresenter(SendTcmpMessagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void unregisterPresenter() {
        this.presenter = null;
    }

    @Override
    public void enableRepeat(boolean animated) {

    }

    @Override
    public void disableRepeat(boolean animated) {

    }
}
