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
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.KeySetting;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DetailAdapterCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.littleviews.BytePickerView;

import butterknife.ButterKnife;

public class ReadClassicCommandAdapter implements CommandDetailViewAdapter {
    public interface ReadClassicCommand extends DetailAdapterCommand {
        TCMPMessage getMessage(byte timeout, byte start, byte end, byte keySetting, byte[] key);
    }

    String infiniteLabel;
    String secondLabel;

    private static final byte[] DEFAULT_KEY = new byte[] {(byte)0xFF,(byte)0xFF,(byte)0xFF,
            (byte)0xFF,(byte)0xFF,(byte)0xFF};

    private static final String KEY_PROGRESS = "KEY_PROGRESS";

    private static final String KEY_START_PAGE = "KEY_START_PAGE";
    private static final String KEY_END_PAGE = "KEY_END_PAGE";

    protected ReadClassicCommand command;

    public ReadClassicCommandAdapter(ReadClassicCommand command) {
        this.command = command;
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent,Bundle bundle) {
        SeekBar seekBar = ButterKnife.findById(parameterParent,R.id.seeker_polling_time);
        BytePickerView startPageView = ButterKnife.findById(parameterParent,R.id.bpv_start_page);
        BytePickerView endPageView = ButterKnife.findById(parameterParent,R.id.bpv_end_page);

        if(seekBar != null)
            bundle.putInt(KEY_PROGRESS,seekBar.getProgress());

        if(startPageView != null)
            bundle.putInt(KEY_START_PAGE,startPageView.getValue());

        if(endPageView != null)
            bundle.putInt(KEY_END_PAGE,endPageView.getValue());
    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent,Bundle bundle) {
        SeekBar seekBar = ButterKnife.findById(parameterParent,R.id.seeker_polling_time);
        BytePickerView startPageView = ButterKnife.findById(parameterParent,R.id.bpv_start_page);
        BytePickerView endPageView = ButterKnife.findById(parameterParent,R.id.bpv_end_page);

        if(bundle.containsKey(KEY_PROGRESS) && seekBar != null) {
            seekBar.setProgress(bundle.getInt(KEY_PROGRESS));
        }

        if(bundle.containsKey(KEY_START_PAGE) && startPageView != null) {
            startPageView.setValue(bundle.getInt(KEY_START_PAGE));
        }

        if(bundle.containsKey(KEY_END_PAGE) && endPageView != null) {
            endPageView.setValue(bundle.getInt(KEY_END_PAGE));
        }
    }

    @Override
    public int getTitleRes() {
        return command.getItem().getTitleRes();
    }

    @Override
    public int getDescriptionRes() {
        return command.getItem().getDescriptionRes();
    }

    @Override
    public void attachParameterView(ViewGroup parent) {
        Context ctx = parent.getContext();
        secondLabel = ctx.getString(R.string.second_label);
        infiniteLabel = ctx.getString(R.string.infinity_label);

        LayoutInflater inflater =LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.command_options_read_classic, parent);

        final TextView timeOutLabel = ButterKnife.findById(v, R.id.tv_label_timeout);
        SeekBar seekBar = ButterKnife.findById(v,R.id.seeker_polling_time);

        int currentValue = seekBar.getProgress();
        setTimeoutLabelForValue(timeOutLabel, currentValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTimeoutLabelForValue(timeOutLabel, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final BytePickerView startPageView = ButterKnife.findById(v,R.id.bpv_start_page);
        final BytePickerView endPageView = ButterKnife.findById(v,R.id.bpv_end_page);
        startPageView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int endValue = endPageView.getValue();

                int difference = newVal - endValue;
                if(difference > 1) {
                    endPageView.setValue(newVal - 1);
                    endPageView.changeCurrentByOne(true);
                }
                else if (difference > 0) {
                    endPageView.changeCurrentByOne(true);
                }
            }
        });

        endPageView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int startValue = startPageView.getValue();

                int difference = startValue - newVal;
                if(difference > 1) {
                    startPageView.setValue(newVal + 1);
                    startPageView.changeCurrentByOne(false);
                }
                else if (difference > 0) {
                    startPageView.changeCurrentByOne(false);
                }
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
        SeekBar seekBar = ButterKnife.findById(parameterViewParent, R.id.seeker_polling_time);

        int timeValue = seekBar.getProgress() + 1;
        timeValue = timeValue == 11 ? 0: timeValue;

        final BytePickerView startPageView = ButterKnife.findById(parameterViewParent,R.id.bpv_start_page);
        final BytePickerView endPageView = ButterKnife.findById(parameterViewParent,R.id.bpv_end_page);


        return command.getMessage((byte) timeValue,(byte)startPageView.getValue(),(byte)endPageView.getValue(), KeySetting.KEY_A,DEFAULT_KEY);
    }
}
