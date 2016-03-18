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
import android.widget.Toast;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DetailAdapterCommand;
import com.taptrack.tcmptappy.utils.ByteUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class HexCommandAdapter implements CommandDetailViewAdapter {
    public interface HexCommand extends DetailAdapterCommand {
        TCMPMessage getMessage(byte[] hex);
    }
    private static final String KEY_HEX_CONTENT = "KEY_URL";

    private HexCommand command;

    public HexCommandAdapter(HexCommand item) {
        this.command = item;
    }

    @Override
    public void storeTransientDataToBundle(ViewGroup parameterParent,Bundle bundle) {
        EditText editText = ButterKnife.findById(parameterParent,R.id.et_text);

        if(editText != null)
            bundle.putString(KEY_HEX_CONTENT, editText.getText().toString());

    }

    @Override
    public void restoreTransientDataFromBundle(ViewGroup parameterParent,Bundle bundle) {
        EditText editText = ButterKnife.findById(parameterParent,R.id.et_text);
        if(bundle.containsKey(KEY_HEX_CONTENT) && editText != null) {
            editText.setText(bundle.getString(KEY_HEX_CONTENT, ""));
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

        LayoutInflater inflater =LayoutInflater.from(ctx);
        View v = inflater.inflate(R.layout.command_options_send_hex, parent);

        final TextInputLayout til = ButterKnife.findById(v,R.id.til_text_holder);
        til.setError(ctx.getString(R.string.error_command_must_be_hex));
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

    Pattern p = Pattern.compile("[0-9a-fA-F]+");
    private boolean isTextValid(String rawText) {
        if(rawText == null)
            return false;
        if(rawText.length() == 0)
            return false;
        if(rawText.length() % 2 != 0)
            return false;
        Matcher m = p.matcher(rawText);
        if(m.matches())
            return true;
        else
            return false;
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

        String text = editText.getText().toString().toLowerCase();

        if(!isTextValid(text)) {
            emphasizeUserError(til);
        }
        else {
            return command.getMessage(ByteUtils.hexStringToByteArray(text));
        }

        return null;

    }
}
