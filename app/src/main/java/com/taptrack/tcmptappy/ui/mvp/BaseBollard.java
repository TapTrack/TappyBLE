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

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.Set;

public class BaseBollard extends Fragment implements MVPHostContext.MVPContextLifecycleCallback {
    protected Set<Presenter> presenters = new HashSet<>(1);

    protected MVPHostContext.MVPContextLifecycleCallback hostContext = new MVPHostContext.MVPContextLifecycleCallback() {
        @Override
        public void onContextResume() {
            BaseBollard.this.onContextResume();
            for (Presenter presenter : presenters) {
                presenter.onContextResume();
            }
        }

        @Override
        public void onContextPause() {
            BaseBollard.this.onContextPause();
            for (Presenter presenter : presenters) {
                presenter.onContextPause();
            }
        }

        @Override
        public void onContextSaveState() {
            BaseBollard.this.onContextSaveState();
            for (Presenter presenter : presenters) {
                presenter.onContextSaveState();
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof MVPHostContext) {
            ((MVPHostContext) context).registerMVPActivityLifecycleCallback(hostContext);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void registerPresenterForCallbacks(Presenter presenter) {
        presenters.add(presenter);
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
