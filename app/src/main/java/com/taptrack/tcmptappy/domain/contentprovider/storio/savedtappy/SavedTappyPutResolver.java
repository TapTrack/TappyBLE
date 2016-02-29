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

package com.taptrack.tcmptappy.domain.contentprovider.storio.savedtappy;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;
import com.taptrack.tcmptappy.data.SavedTappyDefinition;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.SavedTappyPersistenceContract;

public class SavedTappyPutResolver extends DefaultPutResolver<SavedTappyDefinition> {

    @NonNull
    @Override
    protected InsertQuery mapToInsertQuery(@NonNull SavedTappyDefinition object) {
        return InsertQuery.builder()
                .uri(TappyBleDemoProvider.URI_SAVED).build();
    }

    @NonNull
    @Override
    protected UpdateQuery mapToUpdateQuery(@NonNull SavedTappyDefinition object) {
        return UpdateQuery.builder()
                .uri(TappyBleDemoProvider.URI_SAVED)
                .where(SavedTappyPersistenceContract.ADDRESS + "=?")
                .whereArgs(object.getAddress())
                .build();
    }

    @NonNull
    @Override
    protected ContentValues mapToContentValues(@NonNull SavedTappyDefinition object) {
        ContentValues cv = new ContentValues();
        cv.put(SavedTappyPersistenceContract.NAME,object.getName());
        cv.put(SavedTappyPersistenceContract.ADDRESS,object.getAddress());
        cv.put(SavedTappyPersistenceContract.SERVICE_UUID,object.getSerialServiceUuid().toString());
        cv.put(SavedTappyPersistenceContract.RX_UUID,object.getRxCharacteristicUuid().toString());
        cv.put(SavedTappyPersistenceContract.TX_UUID,object.getTxCharacteristicUuid().toString());
        return cv;
    }
}
