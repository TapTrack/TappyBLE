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

package com.taptrack.tcmptappy.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.taptrack.tcmptappy.application.TcmpTappyDemo;
import com.taptrack.tcmptappy.ui.mvp.MVPHostContext;

import java.util.HashSet;
import java.util.Set;

public class BaseActivity extends AppCompatActivity implements MVPHostContext {
    Set<MVPContextLifecycleCallback> mvpActivityLifecycleCallbackSet = new HashSet<>(1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TcmpTappyDemo) getApplicationContext()).bindToTappyManagerIfNecessary();
    }

    @Override
    public void registerMVPActivityLifecycleCallback(MVPContextLifecycleCallback callback) {
        mvpActivityLifecycleCallbackSet.add(callback);
    }

    @Override
    public void unregisterMVPActivityLifecycleCallback(MVPContextLifecycleCallback callback) {
        mvpActivityLifecycleCallbackSet.remove(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(MVPContextLifecycleCallback contextLifecycleCallback : mvpActivityLifecycleCallbackSet) {
            contextLifecycleCallback.onContextResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for(MVPContextLifecycleCallback contextLifecycleCallback : mvpActivityLifecycleCallbackSet) {
            contextLifecycleCallback.onContextPause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for(MVPContextLifecycleCallback contextLifecycleCallback : mvpActivityLifecycleCallbackSet) {
            contextLifecycleCallback.onContextSaveState();
        }
    }


}
