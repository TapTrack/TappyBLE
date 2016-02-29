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

package com.taptrack.tcmptappy.ui.activities.interacttappy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.ui.activities.searchfortappies.SearchTappiesActivity;
import com.taptrack.tcmptappy.ui.base.BaseActivity;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationContainer;
import com.taptrack.tcmptappy.ui.modules.mainnavigationbar.MainNavigationVista;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.SendTcmpMessageVista;
import com.taptrack.tcmptappy.ui.modules.tcmpmessagelist.DisplayTcmpMessageVista;
import com.taptrack.tcmptappy.ui.mvp.BackHandler;
import com.taptrack.tcmptappy.ui.mvp.TransientStatePersistable;
import com.taptrack.tcmptappy.utils.MarshmallowCompatBlePermDelegate;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InteractTappyActivity extends BaseActivity implements MainNavigationContainer {
    private static final String TAG_BOLLARD = "BOLLARD";
    public static final String HAS_ANIMATED = "HAS_ANIMATED";

    private InteractTappyActivityBollard bollard;

    @Bind(R.id.main_navigation_view)
    MainNavigationVista mainNavigationView;

    @Nullable
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.tcmp_message_view)
    DisplayTcmpMessageVista tcmpVista;

    @Bind(R.id.bottom_command_sheet)
    SendTcmpMessageVista bottomSheet;

    private MarshmallowCompatBlePermDelegate blePermDelegate;

    private boolean animated = false;

    private static final int START_DELAY = 300;
    private static final int DURATION_INITIAL = 400;
    private static final int DURATION_NEXT_VIEW_FACTOR = 30;
    private static final float INTERPOLATOR_FACTOR = 2f;

    private final String KEY_MESSAGE_SELECTOR_STATE = "SHEET_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interact_tappy);

        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            animated = savedInstanceState.getBoolean(HAS_ANIMATED,false);
        }

        if(savedInstanceState != null &&
                savedInstanceState.containsKey(KEY_MESSAGE_SELECTOR_STATE) &&
                bottomSheet instanceof TransientStatePersistable) {
            Bundle selectorState = savedInstanceState.getBundle(KEY_MESSAGE_SELECTOR_STATE);
            ((TransientStatePersistable) bottomSheet).restoreTransientState(selectorState);
        }

        setSupportActionBar(toolbar);

        if(drawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }
        else {
            ActionBar bar = getSupportActionBar();
            if(bar != null)
                bar.setDisplayHomeAsUpEnabled(false);
        }

        blePermDelegate = new MarshmallowCompatBlePermDelegate(this);
        blePermDelegate.onCreate();

        FragmentManager fm = getSupportFragmentManager();
        bollard = (InteractTappyActivityBollard) fm.findFragmentByTag(TAG_BOLLARD);
        if(bollard == null) {
            bollard = new InteractTappyActivityBollard();
            fm.beginTransaction()
                    .add(bollard,TAG_BOLLARD)
                    .commit();
        }

        bollard.attachMainNavigationVista(mainNavigationView);
        bollard.attachTcmpMessageVista(tcmpVista);
        bollard.attachSendTcmpMessageVista(bottomSheet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        blePermDelegate.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        blePermDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        blePermDelegate.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (bottomSheet instanceof BackHandler) {
            ((BackHandler) bottomSheet).onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void displayTooManyTappies(int maxTappies) {
        new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(R.string.too_many_tappies_error)
                .build()
                .show();
    }

    @Override
    public void requestStartTappySearch() {
        Intent intent = new Intent(this,SearchTappiesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && !animated) {
            startSlideInAnimation();
            animated = true;
        }
    }

    private void startSlideInAnimation() {
        ViewGroup windowRoot = (ViewGroup) findViewById(android.R.id.content);
        ViewGroup contentRoot = (ViewGroup) windowRoot.getChildAt(0);

        for (int i = 0; i < contentRoot.getChildCount(); i++) {
            View v = contentRoot.getChildAt(i);

            animateSingleView(windowRoot, i, v);
        }
    }

    private void animateSingleView(ViewGroup windowRoot, int viewPosition, View view) {
        view.setTranslationY(windowRoot.getHeight());
        view.setAlpha(0);

        view.animate()
                .translationY(0)
                .alpha(1)
                .setStartDelay(START_DELAY)
                .setDuration(DURATION_INITIAL + DURATION_NEXT_VIEW_FACTOR * viewPosition)
                .setInterpolator(new DecelerateInterpolator(INTERPOLATOR_FACTOR)).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(bottomSheet instanceof TransientStatePersistable) {
            Bundle bundle = new Bundle();
            ((TransientStatePersistable) bottomSheet).storeTransientState(bundle);
            outState.putBundle(KEY_MESSAGE_SELECTOR_STATE, bundle);
        }
        outState.putBoolean(HAS_ANIMATED,animated);
        super.onSaveInstanceState(outState);
    }
}
