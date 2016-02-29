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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.taptrack.tcmptappy.domain.contentprovider.db.TappyBleDemoDbOpenHelper;
import com.taptrack.tcmptappy.domain.contentprovider.meta.ActiveTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.contentprovider.meta.SavedTappyPersistenceContract;
import com.taptrack.tcmptappy.domain.contentprovider.meta.TcmpMessagePersistenceContract;

import javax.inject.Inject;

public class TappyBleDemoProvider extends ContentProvider {
    @NonNull
    public static final String AUTHORITY = "com.taptrack.tcmptappy.provider";

    private static final String PATH_ACTIVE = "activetappy";
    private static final int URI_MATCHER_CODE_ACTIVE = 1;

    private static final String PATH_SAVED = "savedtappy";
    private static final int URI_MATCHER_CODE_SAVED = 2;

    private static final String PATH_MESSAGE = "messages";
    private static final int URI_MATCHER_CODE_MESSAGE = 3;

    private static final UriMatcher URI_MATCHER = new UriMatcher(1);

    public static final Uri URI_ACTIVE;
    public static final Uri URI_SAVED;
    public static final Uri URI_TCMP;

    static {
        URI_MATCHER.addURI(AUTHORITY,PATH_ACTIVE,URI_MATCHER_CODE_ACTIVE);
        URI_MATCHER.addURI(AUTHORITY,PATH_SAVED,URI_MATCHER_CODE_SAVED);
        URI_MATCHER.addURI(AUTHORITY,PATH_MESSAGE,URI_MATCHER_CODE_MESSAGE);

        URI_ACTIVE = new Uri.Builder()
                .authority(AUTHORITY)
                .scheme("content")
                .encodedPath(PATH_ACTIVE)
                .build();
        URI_SAVED = new Uri.Builder()
                .authority(AUTHORITY)
                .scheme("content")
                .encodedPath(PATH_SAVED)
                .build();
        URI_TCMP = new Uri.Builder()
                .authority(AUTHORITY)
                .scheme("content")
                .encodedPath(PATH_MESSAGE)
                .build();
    }

    @Inject
    TappyBleDemoDbOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
//        ((TcmpTappyDemo) getContext().getApplicationContext())
//                .getAppComponent()
//                .inject(this);
//        this
        dbHelper = new TappyBleDemoDbOpenHelper(getContext());
        return true;
    }

    protected void notifyOnUri(Uri uri) {
        Context ctx = getContext();

        // this is probably unnecessary
        // I dont think you can end up calling one of the
        // methods that uses this on an unstarted content provider
        if(ctx != null)  {
            ContentResolver resolver = ctx.getContentResolver();
            if(resolver != null)
                resolver.notifyChange(uri,null);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch(URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_ACTIVE: {
                return dbHelper
                        .getReadableDatabase()
                        .query(
                                ActiveTappyPersistenceContract.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
            }
            case URI_MATCHER_CODE_SAVED: {
                return dbHelper
                        .getReadableDatabase()
                        .query(
                                SavedTappyPersistenceContract.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
            }
            case URI_MATCHER_CODE_MESSAGE: {
                return dbHelper
                        .getReadableDatabase()
                        .query(
                                TcmpMessagePersistenceContract.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );

            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_ACTIVE: {
                return "vnd.android.cursor.dir/activetappy";
            }
            case URI_MATCHER_CODE_SAVED: {
                return "vnd.android.cursor.dir/savedtappy";
            }
            case URI_MATCHER_CODE_MESSAGE: {
                return "vnd.android.cursor.dir/messagetappy";
            }
            default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch(URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_ACTIVE: {
                dbHelper
                        .getWritableDatabase()
                        .insert(
                                ActiveTappyPersistenceContract.TABLE_NAME,
                                null,
                                values);
                break;
            }
            case URI_MATCHER_CODE_SAVED: {
                dbHelper
                        .getWritableDatabase()
                        .insert(
                                SavedTappyPersistenceContract.TABLE_NAME,
                                null,
                                values);

                break;
            }
            case URI_MATCHER_CODE_MESSAGE: {
                dbHelper
                        .getWritableDatabase()
                        .insert(
                                TcmpMessagePersistenceContract.TABLE_NAME,
                                null,
                                values);
                break;
            }
            default: {
                break;
            }
        }

        notifyOnUri(uri);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int result = 0;
        switch(URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_ACTIVE: {
                result = dbHelper
                        .getWritableDatabase()
                        .delete(
                                ActiveTappyPersistenceContract.TABLE_NAME,
                                selection,
                                selectionArgs);
                break;
            }
            case URI_MATCHER_CODE_SAVED: {
                result = dbHelper
                        .getWritableDatabase()
                        .delete(
                                SavedTappyPersistenceContract.TABLE_NAME,
                                selection,
                                selectionArgs);
                break;
            }
            case URI_MATCHER_CODE_MESSAGE: {
                result = dbHelper
                        .getWritableDatabase()
                        .delete(
                                TcmpMessagePersistenceContract.TABLE_NAME,
                                selection,
                                selectionArgs);
                break;
            }
            default: {
                break;
            }
        }

        notifyOnUri(uri);
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result = 0;
        switch(URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_ACTIVE: {
                result = dbHelper.getWritableDatabase().update(
                        ActiveTappyPersistenceContract.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case URI_MATCHER_CODE_SAVED: {
                result = dbHelper.getWritableDatabase().update(
                        SavedTappyPersistenceContract.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case URI_MATCHER_CODE_MESSAGE: {
                result = dbHelper.getWritableDatabase().update(
                        TcmpMessagePersistenceContract.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default: {
                result = 0;
                break;
            }
        }

        notifyOnUri(uri);
        return result;
    }
}
