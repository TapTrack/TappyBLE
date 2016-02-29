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

package com.taptrack.tcmptappy.domain.contentprovider.meta;

import android.provider.BaseColumns;

public class SavedTappyPersistenceContract implements BaseColumns {
    public static final String TABLE_NAME = "savedtappies";
    private static final String PREFIX = "svdtpy_";

    public static final String NAME = PREFIX+"name";
    public static final String ADDRESS = PREFIX+"address";
    public static final String SERVICE_UUID = PREFIX+"serviceuuid";
    public static final String RX_UUID = PREFIX+"rxuuid";
    public static final String TX_UUID = PREFIX+"txuuid";

    public static final String[] ALL_PROJECTION = new String[]{
            NAME,ADDRESS,SERVICE_UUID,RX_UUID,TX_UUID
    };

    public static String getCreateSql() {
        return String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, " +
                        "%s TEXT NOT NULL,  %s TEXT NOT NULL," +
                        "%s TEXT NOT NULL,  %s TEXT NOT NULL,  %s TEXT NOT NULL)",
                TABLE_NAME, _ID,
                NAME,ADDRESS,
                SERVICE_UUID,RX_UUID,TX_UUID);
    }

    public static String getDeleteSql() {
        return String.format("DROP TABLE %S",TABLE_NAME);
    }


}
