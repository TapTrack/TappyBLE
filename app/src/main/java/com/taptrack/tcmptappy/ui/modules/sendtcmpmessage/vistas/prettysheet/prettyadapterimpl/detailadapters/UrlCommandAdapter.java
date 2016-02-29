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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.WriteNdefUriRecordCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;

import butterknife.ButterKnife;

public class UrlCommandAdapter extends NoParameterAdapter {
    String infiniteLabel;
    String secondLabel;

    private static final String KEY_URL_CONTENT = "KEY_URL";
    private static final String KEY_PROGRESS = "KEY_PROGRESS";

    public UrlCommandAdapter(CommandItem item) {
        super(item);
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent,Bundle bundle) {
        EditText editText = ButterKnife.findById(parameterParent,R.id.et_text);
        SeekBar seekBar = ButterKnife.findById(parameterParent,R.id.seeker_polling_time);

        if(editText != null)
            bundle.putString(KEY_URL_CONTENT,editText.getText().toString());

        if(seekBar != null)
            bundle.putInt(KEY_PROGRESS,seekBar.getProgress());
    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent,Bundle bundle) {
        EditText editText = ButterKnife.findById(parameterParent,R.id.et_text);
        SeekBar seekBar = ButterKnife.findById(parameterParent,R.id.seeker_polling_time);
        if(bundle.containsKey(KEY_URL_CONTENT) && editText != null) {
            editText.setText(bundle.getString(KEY_URL_CONTENT,
                    parameterParent.getContext().getString(R.string.url_prepopulate)));
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

        final TextInputLayout til = ButterKnife.findById(v,R.id.til_text_holder);
        til.setError(ctx.getString(R.string.unsupported_url_prefix));
        EditText text = ButterKnife.findById(v,R.id.et_text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isTextValid(s.toString()))
                    til.setErrorEnabled(false);
                else
                    til.setErrorEnabled(true);
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

    private boolean isTextValid(String rawText) {
        String text = rawText.toLowerCase();
        return text.startsWith("http://") ||
                text.startsWith("https://") ||
                text.startsWith("http://www.") ||
                text.startsWith("https://www.") ||
                text.startsWith("tel:") ||
                text.startsWith("mailto:");
    }

    private void emphasizeUserError(ViewGroup v) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Context ctx = v.getContext();
            final ViewGroupOverlay overlay = v.getOverlay();

            final View revealView = new View(v.getContext());
            revealView.setBottom(v.getHeight());
            revealView.setRight(v.getWidth());
            revealView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorErrorTrans));

            overlay.add(revealView);

            float radius = (float) Math.sqrt(Math.pow(v.getHeight(), 2) + Math.pow(v.getWidth(), 2));
            Animator revealAnimator =
                    ViewAnimationUtils.createCircularReveal(revealView,
                            (revealView.getWidth() / 2), revealView.getHeight(), 0.0f, radius);
            //TODO: move this into resources
            revealAnimator.setDuration(500);

            Animator alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
            alphaAnimator.setDuration(200);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(revealAnimator).before(alphaAnimator);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    overlay.remove(revealView);
                }
            });

            animatorSet.start();
        }
        else {
            Toast.makeText(v.getContext(),R.string.unsupported_url_prefix,Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public TCMPMessage userDesiresSend(View parameterViewParent) {
        TextInputLayout til = ButterKnife.findById(parameterViewParent,R.id.til_text_holder);
        EditText editText = ButterKnife.findById(parameterViewParent,R.id.et_text);
        SeekBar seekBar = ButterKnife.findById(parameterViewParent,R.id.seeker_polling_time);

        String text = editText.getText().toString().toLowerCase();
        int timeValue = seekBar.getProgress() + 1;
        timeValue = timeValue == 11 ? 0: timeValue;

        if(text.startsWith("http://")) {
            return new WriteNdefUriRecordCommand((byte) timeValue,
                    false,
                    NdefUriCodes.URICODE_HTTP,
                    text.substring("http://".length()).getBytes());
        }
        else if(text.startsWith("https://")) {
            return new WriteNdefUriRecordCommand((byte)timeValue,
                    false,
                    NdefUriCodes.URICODE_HTTPS,
                    text.substring("https://".length()).getBytes());
        }
        else if(text.startsWith("http://www.")) {
            return new WriteNdefUriRecordCommand((byte)timeValue,
                    false,
                    NdefUriCodes.URICODE_HTTPWWW,
                    text.substring("http://www.".length()).getBytes());
        }
        else if(text.startsWith("https://www.")) {
            return new WriteNdefUriRecordCommand((byte)timeValue,
                    false,
                    NdefUriCodes.URICODE_HTTPSWWW,
                    text.substring("https://www.".length()).getBytes());
        }
        else if(text.startsWith("tel:")) {
            return new WriteNdefUriRecordCommand((byte)timeValue,
                    false,
                    NdefUriCodes.URICODE_TEL,
                    text.substring("tel:".length()).getBytes());
        }
        else if(text.startsWith("mailto:")) {
            return new WriteNdefUriRecordCommand((byte) timeValue,
                    false,
                    NdefUriCodes.URICODE_MAILTO,
                    text.substring("mailto:".length()).getBytes());
        }
        else {
            emphasizeUserError(til);
        }


        return null;

    }
}
