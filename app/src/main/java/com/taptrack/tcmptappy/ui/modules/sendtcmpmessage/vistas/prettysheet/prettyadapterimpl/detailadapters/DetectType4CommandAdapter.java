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

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DetailAdapterCommand;
import com.taptrack.tcmptappy.utils.ByteUtils;

import butterknife.ButterKnife;

public class DetectType4CommandAdapter implements CommandDetailViewAdapter {
    public interface DetectType4Command extends DetailAdapterCommand {
        TCMPMessage getMessage(byte timeout, boolean typeA, @Nullable Byte afi);
    }

    String infiniteLabel;
    String secondLabel;

    private static final String KEY_PROGRESS = "KEY_PROGRESS";
    private static final String KEY_MODULATION = "KEY_MODULATION";
    private static final String KEY_AFI = "KEY_AFI";

    protected DetectType4Command command;

    private ValueAnimator afiAnimator;

    private static final float AFI_ANIMATE_SPEED = 1f;

    public DetectType4CommandAdapter(DetectType4Command command) {
        this.command = command;
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent, Bundle bundle) {
        SeekBar seekBar = ButterKnife.findById(parameterParent, R.id.seeker_polling_time);
        RadioGroup modulationGroup = ButterKnife.findById(parameterParent, R.id.rg_modulation_type);
        EditText afiText = ButterKnife.findById(parameterParent, R.id.et_afi);

        if (seekBar != null) {
            bundle.putInt(KEY_PROGRESS, seekBar.getProgress());
        }

        if (modulationGroup != null) {
            bundle.putInt(KEY_MODULATION, modulationGroup.getCheckedRadioButtonId());
        }

        if (afiText != null) {
            bundle.putString(KEY_AFI, afiText.getText().toString());
        }

    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent, Bundle bundle) {
        SeekBar seekBar = ButterKnife.findById(parameterParent, R.id.seeker_polling_time);
        RadioGroup modulationGroup = ButterKnife.findById(parameterParent, R.id.rg_modulation_type);
        FrameLayout afiHolder = ButterKnife.findById(parameterParent, R.id.fl_afi_holder);
        EditText afiText = ButterKnife.findById(parameterParent, R.id.et_afi);

        if (bundle.containsKey(KEY_PROGRESS) && seekBar != null) {
            seekBar.setProgress(bundle.getInt(KEY_PROGRESS));
        }

        if (bundle.containsKey(KEY_MODULATION) && modulationGroup != null) {
            int buttonId = bundle.getInt(KEY_MODULATION);
            if (buttonId != -1) {

                // this check shouldn't be necessary
                if (buttonId == R.id.rb_modulation_type_a) {
                    RadioButton bt = ((RadioButton) modulationGroup.findViewById(buttonId));
                    bt.setChecked(true);
                    hideAfi(afiHolder, false);
                } else {
                    RadioButton bt = ((RadioButton) modulationGroup.findViewById(buttonId));
                    bt.setChecked(true);
                    showAfi(parameterParent, afiHolder, false);
                }
            }
        }

        if (bundle.containsKey(KEY_AFI) && afiText != null) {
            afiText.setText(bundle.getString(KEY_AFI));
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

        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View v = inflater.inflate(R.layout.command_options_detect_type4, parent);

        final TextView timeOutLabel = ButterKnife.findById(v, R.id.tv_label_timeout);
        SeekBar seekBar = ButterKnife.findById(v, R.id.seeker_polling_time);

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

        final FrameLayout afiHolder = ButterKnife.findById(v, R.id.fl_afi_holder);
        RadioGroup modulationGroup = ButterKnife.findById(v, R.id.rg_modulation_type);
        modulationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_modulation_type_a) {
                    hideAfi(afiHolder, true);
                } else {
                    showAfi(v,afiHolder, true);
                }
            }
        });

        final TextInputLayout til = ButterKnife.findById(v, R.id.til_afi);
        EditText et = ButterKnife.findById(v, R.id.et_afi);

        til.setErrorEnabled(false);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (til.isErrorEnabled()) {
                    if (s.length() == 0) {
                        til.setErrorEnabled(false);
                    } else {
                        Byte afi = parseAfi(str);
                        if (afi != null) {
                            til.setErrorEnabled(false);
                        }
                    }
                }
            }
        });

    }

    private void showAfi(View afiHolderParent, View afiHolder, boolean animate) {
        if(afiAnimator != null && afiAnimator.isStarted()) {
            afiAnimator.cancel();
        }
        if (!animate) {
            ViewGroup.LayoutParams params = afiHolder.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            afiHolder.setLayoutParams(params);
        } else {
            int currentHeight = afiHolder.getLayoutParams().height;

            afiHolder.getLayoutParams().height = 0;
            afiHolder.measure(
                    View.MeasureSpec.makeMeasureSpec(
                            afiHolderParent.getMeasuredWidth(),
                            View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(
                            0,
                            View.MeasureSpec.UNSPECIFIED));
            int newHeight = afiHolder.getMeasuredHeight();

            afiAnimator = createBaseAfiAnimator(currentHeight,newHeight,AFI_ANIMATE_SPEED,afiHolder);
            afiAnimator.start();
        }
    }

    private void hideAfi(View afiHolder, boolean animate) {
        if(afiAnimator != null && afiAnimator.isStarted()) {
            afiAnimator.cancel();
        }
        if (!animate) {
            ViewGroup.LayoutParams params = afiHolder.getLayoutParams();
            params.height = 0;

            afiHolder.setLayoutParams(params);
        } else {
            int currentHeight = afiHolder.getLayoutParams().height;
            afiAnimator = createBaseAfiAnimator(currentHeight,0,AFI_ANIMATE_SPEED,afiHolder);
            afiAnimator.start();
        }
    }

    private ValueAnimator createBaseAfiAnimator(int current,
                                                int desired,
                                                float velocity,
                                                final View afiView) {
        int difference = Math.abs(desired - current);
        long duration = (long) (difference / velocity);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(current, desired);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new FastOutLinearInInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                afiView.getLayoutParams().height = (int) animation.getAnimatedValue();
                afiView.requestLayout();
            }
        });
        return valueAnimator;
    }

    private void setTimeoutLabelForValue(TextView view, int value) {
        view.setText(String.format(view.getContext().getString(R.string.timeout_label), getStringForValue(value)));
    }

    private String getStringForValue(int currentValue) {
        if (currentValue == 10) {
            return infiniteLabel;
        } else {
            return String.format(secondLabel, (currentValue + 1));
        }
    }

    private void emphasizeAfiError(View v) {
        TextInputLayout til = ButterKnife.findById(v, R.id.til_afi);
        til.setError(v.getContext().getString(R.string.invalid_afi_error_message));
        til.setErrorEnabled(true);
    }

    @Nullable
    private Byte parseAfi(String afiStr) {
        if (afiStr.length() == 2 && afiStr.matches("-?[0-9a-fA-F]+")) {
            byte[] bytes = ByteUtils.hexStringToByteArray(afiStr);

            //this should be always true
            if (bytes.length == 1) {
                return bytes[0];
            }
        }
        return null;
    }

    @Nullable
    @Override
    public TCMPMessage userDesiresSend(View parameterViewParent) {
        SeekBar seekBar = ButterKnife.findById(parameterViewParent, R.id.seeker_polling_time);
        RadioGroup radioGroup = ButterKnife.findById(parameterViewParent, R.id.rg_modulation_type);
        EditText afiText = ButterKnife.findById(parameterViewParent, R.id.et_afi);


        int timeValue = seekBar.getProgress() + 1;
        timeValue = timeValue == 11 ? 0 : timeValue;

        boolean modulationTypeA = radioGroup.getCheckedRadioButtonId() == R.id.rb_modulation_type_a;
        String afiStr = afiText.getText().toString();
        Byte afi = null;
        if (!modulationTypeA && afiStr.length() != 0) {
            afi = parseAfi(afiStr);
            if (afi == null) {
                emphasizeAfiError(parameterViewParent);
                return null;
            }
        }

        return command.getMessage((byte) timeValue, modulationTypeA, afi);
    }
}
