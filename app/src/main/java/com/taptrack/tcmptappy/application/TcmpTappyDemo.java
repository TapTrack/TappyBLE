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

package com.taptrack.tcmptappy.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.taptrack.tappyble.BuildConfig;
import com.taptrack.tcmptappy.domain.tappycommunication.backgroundservices.TappyManagementBgService;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import timber.log.Timber;

public class TcmpTappyDemo extends Application {
    private static AppComponent appComponent;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            writeLock.lock();
            boundTappyManager = (TappyManagementBgService.TappyManagerBinder) service;
            writeLock.unlock();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            writeLock.lock();
            boundTappyManager = null;
            writeLock.unlock();
        }
    };

    boolean isBinding = false;
    private TappyManagementBgService.TappyManagerBinder boundTappyManager;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public static TcmpTappyDemo get(Context ctx) {
        return (TcmpTappyDemo) ctx.getApplicationContext();
    }

    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    //You didn't see this

    /**
     * This strange binder stuff is to make an application-level bind
     * that only exists in the process with the UI.
     *
     * @return Tappy binder or null if not bound
     */
    @Nullable
    public TappyManagementBgService.TappyManagerBinder getTappyBinderOrNull() {
        if(readLock.tryLock()) {
            TappyManagementBgService.TappyManagerBinder binder = boundTappyManager;
            readLock.unlock();
            return binder;
        }
        return null;
    }

    public boolean hasTappyBinder() {
        boolean hasBinder = false;
        if(readLock.tryLock()) {
            hasBinder = boundTappyManager != null;
            readLock.unlock();
        }
        return hasBinder;
    }

    public void bindToTappyManagerIfNecessary() {
        writeLock.lock();
        if(!isBinding) {
            isBinding = true;
            bindService(new Intent(this, TappyManagementBgService.class), connection, BIND_AUTO_CREATE);
        }
        writeLock.unlock();
    }
}
