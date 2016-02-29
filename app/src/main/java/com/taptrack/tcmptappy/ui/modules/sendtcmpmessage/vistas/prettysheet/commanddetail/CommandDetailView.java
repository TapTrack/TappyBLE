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

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.mvp.TransientStatePersistable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommandDetailView extends RelativeLayout implements TransientStatePersistable {
    @Bind(R.id.vg_options_holder)
    ViewGroup optionsHolder;
    @Bind(R.id.tv_command_description)
    TextView descriptionTxt;
    @Bind(R.id.tv_command_name)
    TextView titleText;
    @Bind(R.id.bt_send)
    Button sendButton;
    @Bind(R.id.bt_cancel)
    Button cancelButton;
    @Bind(R.id.v_shim)
    View shim;

    boolean hasInitialized = false;

    LayoutInflater inflater;

    private CommandSendListener listener;
    private CommandDetailViewAdapter adapter;

    private static final String KEY_ADAPTER_STATE = "ADAPTER_STATE";

    @Override
    public void storeTransientState(Bundle bundle) {
        if(adapter != null) {
            Bundle adapterTransientState = new Bundle();
            adapter.storeTransientDataToBundle(optionsHolder,adapterTransientState);
            bundle.putBundle(KEY_ADAPTER_STATE, adapterTransientState);
        }
    }

    @Override
    public void restoreTransientState(Bundle bundle) {
        if(bundle != null && bundle.containsKey(KEY_ADAPTER_STATE) && adapter != null) {
            adapter.restoreTransientDataFromBundle(optionsHolder,bundle.getBundle(KEY_ADAPTER_STATE));
        }
    }

    public interface CommandSendListener {
        public void onValidSendRequest(TCMPMessage message);
        public void onCancel();
    }

    public CommandDetailView(Context context) {
        super(context);
        init();
    }

    public CommandDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommandDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        if(!hasInitialized) {
            inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(R.layout.view_command_detail, this);
            hasInitialized = true;
            ButterKnife.bind(this);
        }
    }

    public void setShimHeight(int height) {
        shim.getLayoutParams().height = height;
        shim.requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //problems with clicking through otherwise
        return true;
    }

    public void clearSendListener() {
        this.listener = null;
    }

    public void setSendListener(CommandSendListener listener) {
        this.listener = listener;
    }

    public void setDetailAdapter(CommandDetailViewAdapter adapter, boolean update) {
        this.adapter = adapter;
        if(update)
            updateFromAdapter();
    }

    public void updateFromAdapter() {
        if(adapter != null) {
            titleText.setText(adapter.getTitleRes());
            descriptionTxt.setText(adapter.getDescriptionRes());
            optionsHolder.removeAllViews();
            adapter.attachParameterView(optionsHolder);
            invalidate();
        }
        else {
            clear();
        }
    }

    public void clear() {
        titleText.setText("");
        descriptionTxt.setText("");
        optionsHolder.removeAllViews();
        invalidate();
    }

    @OnClick(R.id.bt_send)
    protected void send(View v) {
        if(adapter != null) {
            TCMPMessage message = adapter.userDesiresSend(optionsHolder);
            if(message != null && listener != null) {
                listener.onValidSendRequest(message);
            }
        }
    }

    @OnClick(R.id.bt_cancel)
    protected void cancel(View v) {
        if(listener != null)
            listener.onCancel();
    }
}
