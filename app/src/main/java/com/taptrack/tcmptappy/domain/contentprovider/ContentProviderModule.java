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

package com.taptrack.tcmptappy.domain.contentprovider;

import android.content.Context;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.taptrack.tcmptappy.application.AppModule;
import com.taptrack.tcmptappy.dagger.ApplicationScope;
import com.taptrack.tcmptappy.data.ActiveTappyDefinition;
import com.taptrack.tcmptappy.data.SavedTappyDefinition;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.domain.contentprovider.db.TappyBleDemoDbOpenHelper;
import com.taptrack.tcmptappy.domain.contentprovider.storio.activetappy.ActiveTappyDeleteResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.activetappy.ActiveTappyGetResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.activetappy.ActiveTappyPutResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.savedtappy.SavedTappyDeleteResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.savedtappy.SavedTappyGetResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.savedtappy.SavedTappyPutResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.tcmpmessage.TcmpMessageDeleteResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.tcmpmessage.TcmpMessageGetResolver;
import com.taptrack.tcmptappy.domain.contentprovider.storio.tcmpmessage.TcmpMessagePutResolver;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ContentProviderModule {
    @Provides
    @ApplicationScope
    TappyBleDemoDbOpenHelper getDbOpenHelper(@Named(AppModule.NAME_APP_CONTEXT) Context ctx) {
        return new TappyBleDemoDbOpenHelper(ctx);
    }

    @Provides
    @ApplicationScope
    StorIOContentResolver getStorioContentResolver(@Named(AppModule.NAME_APP_CONTEXT) Context ctx) {
        return DefaultStorIOContentResolver.builder()
                .contentResolver(ctx.getContentResolver())
                .addTypeMapping(ActiveTappyDefinition.class,
                        ContentResolverTypeMapping.<ActiveTappyDefinition>builder()
                                .putResolver(new ActiveTappyPutResolver())
                                .getResolver(new ActiveTappyGetResolver())
                                .deleteResolver(new ActiveTappyDeleteResolver())
                                .build())
                .addTypeMapping(SavedTappyDefinition.class,
                        ContentResolverTypeMapping.<SavedTappyDefinition>builder()
                                .putResolver(new SavedTappyPutResolver())
                                .getResolver(new SavedTappyGetResolver())
                                .deleteResolver(new SavedTappyDeleteResolver())
                                .build())
                .addTypeMapping(SavedTcmpMessage.class,
                        ContentResolverTypeMapping.<SavedTcmpMessage>builder()
                                .putResolver(new TcmpMessagePutResolver())
                                .getResolver(new TcmpMessageGetResolver())
                                .deleteResolver(new TcmpMessageDeleteResolver())
                                .build())
                .build();
    }
}
