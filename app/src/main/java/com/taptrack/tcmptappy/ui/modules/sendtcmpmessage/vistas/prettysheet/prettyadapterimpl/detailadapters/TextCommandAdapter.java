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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.WriteNdefTextRecordCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;

import butterknife.ButterKnife;

public class TextCommandAdapter extends NoParameterAdapter {
    String infiniteLabel;
    String secondLabel;

    private static final String KEY_TEXT_CONTENT = "KEY_TEXT";
    private static final String KEY_PROGRESS = "KEY_PROGRESS";

    public TextCommandAdapter(CommandItem item) {
        super(item);
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent,Bundle bundle) {
        EditText editText = ButterKnife.findById(parameterParent,R.id.et_text);
        SeekBar seekBar = ButterKnife.findById(parameterParent,R.id.seeker_polling_time);

        if(editText != null)
            bundle.putString(KEY_TEXT_CONTENT,editText.getText().toString());

        if(seekBar != null)
            bundle.putInt(KEY_PROGRESS,seekBar.getProgress());
    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent,Bundle bundle) {
        EditText editText = ButterKnife.findById(parameterParent,R.id.et_text);
        SeekBar seekBar = ButterKnife.findById(parameterParent,R.id.seeker_polling_time);
        if(bundle.containsKey(KEY_TEXT_CONTENT) && editText != null) {
            editText.setText(bundle.getString(KEY_TEXT_CONTENT));
        }

        if(bundle.containsKey(KEY_PROGRESS) && seekBar != null) {
            seekBar.setProgress(bundle.getInt(KEY_PROGRESS));
        }
    }

    @Override
    public void attachParameterView(ViewGroup parent) {
        Context ctx = parent.getContext();
        secondLabel = ctx.getString(R.string.second_label);
        infiniteLabel = ctx.getString(R.string.infinity_label);

        LayoutInflater inflater =LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.command_options_send_text, parent);

        final TextView timeOutLabel = ButterKnife.findById(v, R.id.tv_label_timeout);
        SeekBar seekBar = ButterKnife.findById(v,R.id.seeker_polling_time);

        int currentValue = seekBar.getProgress();
        setTimeoutLabelForValue(timeOutLabel,currentValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTimeoutLabelForValue(timeOutLabel,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setTimeoutLabelForValue(TextView view, int value) {
        view.setText(String.format(view.getContext().getString(R.string.timeout_label), getStringForValue(value)));
    }

    private String getStringForValue(int currentValue) {
        if(currentValue == 10) {
            return infiniteLabel;
        }
        else {
            return String.format(secondLabel,(currentValue + 1));
        }
    }

    @Nullable
    @Override
    public TCMPMessage userDesiresSend(View parameterViewParent) {
        EditText editText = ButterKnife.findById(parameterViewParent,R.id.et_text);
        SeekBar seekBar = ButterKnife.findById(parameterViewParent,R.id.seeker_polling_time);

        String text = editText.getText().toString();

        int timeValue = seekBar.getProgress() + 1;
        timeValue = timeValue == 11 ? 0: timeValue;

        return new WriteNdefTextRecordCommand((byte) timeValue,false,text.getBytes());
    }
}
