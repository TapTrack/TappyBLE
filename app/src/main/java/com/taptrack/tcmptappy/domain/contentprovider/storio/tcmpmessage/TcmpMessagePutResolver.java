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

package com.taptrack.tcmptappy.domain.contentprovider.storio.tcmpmessage;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.domain.contentprovider.TappyBleDemoProvider;
import com.taptrack.tcmptappy.domain.contentprovider.meta.TcmpMessagePersistenceContract;

public class TcmpMessagePutResolver extends DefaultPutResolver<SavedTcmpMessage>{

    @NonNull
    @Override
    protected InsertQuery mapToInsertQuery(SavedTcmpMessage object) {
        return InsertQuery.builder()
                .uri(TappyBleDemoProvider.URI_TCMP)
                .build();
    }

    @NonNull
    @Override
    protected UpdateQuery mapToUpdateQuery(@NonNull SavedTcmpMessage object) {
        return UpdateQuery.builder()
                .uri(TappyBleDemoProvider.URI_TCMP)
                .where(TcmpMessagePersistenceContract._ID+"=?")
                .whereArgs(object.getDbId())
                .build();
    }

    @NonNull
    @Override
    protected ContentValues mapToContentValues(@NonNull SavedTcmpMessage object) {
        ContentValues contentValues = new ContentValues();
        if(object.getDbId() != null)
            contentValues.put(TcmpMessagePersistenceContract._ID,object.getDbId());

        contentValues.put(TcmpMessagePersistenceContract.NAME,object.getName());
        contentValues.put(TcmpMessagePersistenceContract.ADDRESS,object.getAddress());
        contentValues.put(TcmpMessagePersistenceContract.TIMESTAMP,object.getTimestamp());
        contentValues.put(TcmpMessagePersistenceContract.BYTES,object.getMessage());
        return contentValues;
    }
}
