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

package com.taptrack.tcmptappy.tappyblescanner.scanners.jellybean;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.taptrack.tcmptappy.tappyblescanner.IBluetoothLeScanner;
import com.taptrack.tcmptappy.tappyblescanner.ScanCallback;
import com.taptrack.tcmptappy.tappyblescanner.ScanFilter;
import com.taptrack.tcmptappy.tappyblescanner.ScanRecord;
import com.taptrack.tcmptappy.tappyblescanner.ScanResult;
import com.taptrack.tcmptappy.tappyblescanner.ScanSettings;
import com.taptrack.tcmptappy.tappyblescanner.scanners.ScannerUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class JellybeanBleScanner implements IBluetoothLeScanner, BluetoothAdapter.LeScanCallback {
    protected final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    protected final Map<ScanCallback, ScanCallbackHolder> callbackHolderMap = new ConcurrentHashMap<>();

    protected void throwIfCallbackInUse(ScanCallback callback) {
        if(callbackHolderMap.containsKey(callback))
            throw new IllegalArgumentException("Scan already initiated for that callback");
    }

    protected void updateScanningStatus() {
        //possibly should check if its scanning
        if(callbackHolderMap.isEmpty()) {
            //noinspection deprecation
            bluetoothAdapter.stopLeScan(this);
        }
        else {
            //noinspection deprecation
            bluetoothAdapter.startLeScan(this);
        }
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH})
    public void startScan(@NonNull ScanCallback callback) {
        ScannerUtils.throwOnAdaptorNotEnabled(bluetoothAdapter);
        throwIfCallbackInUse(callback);

        callbackHolderMap.put(callback,new ScanCallbackHolder(callback));

        updateScanningStatus();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH})
    public void startScan(List<ScanFilter> filters, @NonNull ScanSettings settings, @NonNull ScanCallback callback) {
        ScannerUtils.throwOnAdaptorNotEnabled(bluetoothAdapter);
        throwIfCallbackInUse(callback);

        callbackHolderMap.put(callback, new ScanCallbackHolder(callback, settings, filters));

        updateScanningStatus();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH})
    public void stopScan(@NonNull ScanCallback callback) {
        callbackHolderMap.remove(callback);

        updateScanningStatus();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH})
    public void flushPendingScanResults(@NonNull ScanCallback callback) {
        ScannerUtils.throwOnAdaptorNotEnabled(bluetoothAdapter);
        throwIfCallbackInUse(callback);

        ScanCallbackHolder holder;
        holder = callbackHolderMap.get(callback);

        holder.flushPendingResults();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        ScanRecord compatRecord = ScanRecord.parseFromBytes(scanRecord);
        ScanResult compatResult = new ScanResult(device,compatRecord,rssi, SystemClock.elapsedRealtimeNanos());

        Collection<ScanCallbackHolder> holders = callbackHolderMap.values();
        for(ScanCallbackHolder holder: holders){
            holder.handleScanResult(compatResult);
        }
    }

    private class ScanCallbackHolder {
        private ScanCallback compatScanner;
        private ScanSettings compatSettings;
        private List<ScanFilter> compatFilters;

        public ScanCallbackHolder(ScanCallback compatScanner) {
            this.compatScanner = compatScanner;
        }

        public ScanCallbackHolder(ScanCallback compatScanner, ScanSettings compatSettings, List<ScanFilter> compatFilters) {
            this.compatScanner = compatScanner;
            this.compatSettings = compatSettings;
            this.compatFilters = compatFilters;
        }

        public void handleScanResult(ScanResult result) {
            if(compatFilters == null || compatFilters.size() == 0)
                handleFilteredResult(result);

            for(ScanFilter filter : compatFilters) {
                if(filter.matches(result)) {
                    handleFilteredResult(result);
                    break;
                }
            }
        }

        private void handleFilteredResult(ScanResult result) {
            compatScanner.onScanResult(ScanSettings.CALLBACK_TYPE_FIRST_MATCH,result);
        }

        public void flushPendingResults() {

        }
    }
}
