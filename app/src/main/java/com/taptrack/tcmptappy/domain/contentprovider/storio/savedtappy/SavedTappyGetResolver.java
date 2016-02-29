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

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import com.taptrack.tcmptappy.data.SavedTappyDefinition;
import com.taptrack.tcmptappy.domain.contentprovider.meta.SavedTappyPersistenceContract;

import java.util.UUID;

public class SavedTappyGetResolver extends DefaultGetResolver<SavedTappyDefinition> {
    @NonNull
    @Override
    public SavedTappyDefinition mapFromCursor(Cursor cursor) {
        int nameIdx = cursor.getColumnIndex(SavedTappyPersistenceContract.NAME);
        int addressIdx = cursor.getColumnIndex(SavedTappyPersistenceContract.ADDRESS);
        int serviceUuidIdx = cursor.getColumnIndex(SavedTappyPersistenceContract.SERVICE_UUID);
        int rxCharacteristicUuidIdx = cursor.getColumnIndex(SavedTappyPersistenceContract.RX_UUID);
        int txCharacteristicUuidIdx = cursor.getColumnIndex(SavedTappyPersistenceContract.TX_UUID);

        if(nameIdx == -1 ||
                addressIdx == -1 ||
                serviceUuidIdx == -1 ||
                rxCharacteristicUuidIdx == -1 ||
                txCharacteristicUuidIdx == -1) {
            throw new IllegalArgumentException("Cursor missing fields");
        }

        String name = cursor.getString(nameIdx);
        String address = cursor.getString(addressIdx);
        String service = cursor.getString(serviceUuidIdx);
        String rx = cursor.getString(rxCharacteristicUuidIdx);
        String tx = cursor.getString(txCharacteristicUuidIdx);

        UUID serviceUuid = UUID.fromString(service);
        UUID rxUuid = UUID.fromString(rx);
        UUID txUuid = UUID.fromString(tx);

        return new SavedTappyDefinition(name,address,serviceUuid,rxUuid,txUuid);
    }
}
