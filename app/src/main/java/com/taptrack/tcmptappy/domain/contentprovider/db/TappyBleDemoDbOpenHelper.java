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

package com.taptrack.tcmptappy.domain.contentprovider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.taptrack.tcmptappy.domain.contentprovider.meta.ActiveTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.contentprovider.meta.SavedTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.contentprovider.meta.TcmpMessagePersistenceContract;

public class TappyBleDemoDbOpenHelper extends SQLiteOpenHelper {
    protected static final int DB_VERSION = 3;
    protected static final String DB_NAME = "TappyBle.db";

    public TappyBleDemoDbOpenHelper(Context ctx) {
        super(ctx,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ActiveTappyPersistenceContract.getCreateSql());
        db.execSQL(SavedTappyPersistenceContract.getCreateSql());
        db.execSQL(TcmpMessagePersistenceContract.getCreateSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ActiveTappyPersistenceContract.getDeleteSql());
        db.execSQL(SavedTappyPersistenceContract.getDeleteSql());
        db.execSQL(TcmpMessagePersistenceContract.getDeleteSql());
        onCreate(db);
    }
}
