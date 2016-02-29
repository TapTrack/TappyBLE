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

package com.taptrack.tcmptappy.domain.contentprovider.storio.activetappy;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.taptrack.tcmptappy.data.ActiveTappyDefinition;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.ActiveTappyPersistenceContract;

public class ActiveTappyDeleteResolver extends DefaultDeleteResolver<ActiveTappyDefinition> {

    @NonNull
    @Override
    protected DeleteQuery mapToDeleteQuery(ActiveTappyDefinition object) {
        return DeleteQuery.builder()
                .uri(TappyBleDemoProvider.URI_ACTIVE)
                .where(ActiveTappyPersistenceContract.ADDRESS+"=?")
                .whereArgs(object.getAddress())
                .build();
    }
}
