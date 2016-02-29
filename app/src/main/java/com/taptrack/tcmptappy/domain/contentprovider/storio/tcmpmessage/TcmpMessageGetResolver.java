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

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.domain.contentprovider.meta.TcmpMessagePersistenceContract;

public class TcmpMessageGetResolver extends DefaultGetResolver<SavedTcmpMessage> {
//    public static final String NAME = PREFIX+"srcname";
//    public static final String ADDRESS = PREFIX+"srcaddress";
//
//    public static final String TIMESTAMP = PREFIX+"timestamp";
//    public static final String BYTES = PREFIX+"bytes"
    @NonNull
    @Override
    public SavedTcmpMessage mapFromCursor(Cursor cursor) {
        int idIdx = cursor.getColumnIndex(TcmpMessagePersistenceContract._ID);

        int nameIdx = cursor.getColumnIndex(TcmpMessagePersistenceContract.NAME);
        int addressIdx = cursor.getColumnIndex(TcmpMessagePersistenceContract.ADDRESS);

        int timestampIdx = cursor.getColumnIndex(TcmpMessagePersistenceContract.TIMESTAMP);
        int bytesIdx = cursor.getColumnIndex(TcmpMessagePersistenceContract.BYTES);

        if(idIdx == -1 ||
                nameIdx == -1 ||
                addressIdx == -1 ||
                timestampIdx == -1 ||
                bytesIdx == -1) {
            throw new IllegalArgumentException("Cursor missing required fields");
        }

        return new SavedTcmpMessage(cursor.getLong(idIdx),
                cursor.getString(nameIdx),
                cursor.getString(addressIdx),
                cursor.getLong(timestampIdx),
                cursor.getBlob(bytesIdx));
    }
}
