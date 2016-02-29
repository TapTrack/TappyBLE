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

package com.taptrack.tcmptappy.ui.mvp;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends Vista> implements Presenter<V> {
    WeakReference<V> vistaReference;

    @Override
    public void registerVista(V vista) {
        unregisterVista();
        vista.registerPresenter(this);
        vistaReference = new WeakReference<V>(vista);
        onNewVistaAttached();
    }

    @Override
    public void unregisterVista() {
        V vista = getVista();
        if(vista != null)
            vista.unregisterPresenter();
    }

    @Nullable
    public V getVista() {
        if(vistaReference == null)
            return null;
        else
            return vistaReference.get();
    }


    @Override
    public void onContextResume() {

    }

    @Override
    public void onContextPause() {

    }

    @Override
    public void onContextSaveState() {

    }
}
