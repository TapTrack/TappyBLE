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

package com.taptrack.tcmptappy.tappyblescanner.scanners.lollipop;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.taptrack.tcmptappy.tappyblescanner.BluetoothLeScannerCompat;
import com.taptrack.tcmptappy.tappyblescanner.IBluetoothLeScanner;
import com.taptrack.tcmptappy.tappyblescanner.ScanCallback;
import com.taptrack.tcmptappy.tappyblescanner.ScanFilter;
import com.taptrack.tcmptappy.tappyblescanner.ScanSettings;
import com.taptrack.tcmptappy.tappyblescanner.scanners.ScannerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LollipopBleScanner extends BluetoothLeScannerCompat implements IBluetoothLeScanner {
    private final BluetoothAdapter bluetoothAdapter;
    private final Map<ScanCallback,ScanCallbackHolder> callbackHolderMap;

    public LollipopBleScanner() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        callbackHolderMap = new ConcurrentHashMap<>();
    }

    protected void throwIfCallbackInUse(ScanCallback callback) {
        if(callbackHolderMap.containsKey(callback))
            throw new IllegalArgumentException("Scan already initiated for that callback");
    }

    protected BluetoothLeScanner getNativeScannerOrThrow() {
        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        if(scanner == null)
            throw new IllegalStateException("Could not get scanner");
        return scanner;
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH})
    public void startScan(@NonNull ScanCallback callback) {
        ScannerUtils.throwOnAdaptorNotEnabled(bluetoothAdapter);
        final BluetoothLeScanner nativeScanner = getNativeScannerOrThrow();

        throwIfCallbackInUse(callback);

        ScanCallbackHolder holder = new ScanCallbackHolder(callback);
        callbackHolderMap.put(callback,holder);

        nativeScanner.startScan(holder.getNativeCallback());
    }

    @Override
    public void startScan(List<ScanFilter> filters, @NonNull ScanSettings settings, @NonNull ScanCallback callback) {
        ScannerUtils.throwOnAdaptorNotEnabled(bluetoothAdapter);
        final BluetoothLeScanner nativeScanner = getNativeScannerOrThrow();

        throwIfCallbackInUse(callback);

        ScanCallbackHolder holder = new ScanCallbackHolder(callback,settings,filters);
        nativeScanner.startScan(holder.getNativeFilters(), holder.getNativeScanSettings(), holder.getNativeCallback());
    }

    @Override
    public void stopScan(@NonNull ScanCallback callback) {
        ScanCallbackHolder holder = callbackHolderMap.get(callback);
        if(holder == null) //possibly should throw an exception...
            return;

        BluetoothLeScanner scanner = getNativeScannerOrThrow();

        scanner.stopScan(holder.getNativeCallback());
        callbackHolderMap.remove(callback);
    }

    @Override
    public void flushPendingScanResults(@NonNull ScanCallback callback) {
        ScannerUtils.throwOnAdaptorNotEnabled(bluetoothAdapter);
        BluetoothLeScanner nativeScanner = getNativeScannerOrThrow();

        ScanCallbackHolder holder = callbackHolderMap.get(callback);
        if(holder == null)
            throw new IllegalArgumentException("Callback not registered");

        nativeScanner.flushPendingScanResults(holder.getNativeCallback());
    }


    private class ScanCallbackHolder {
        private ScanCallback compatCallback;
        private ScanSettings compatSettings;
        private List<ScanFilter> compatFilters;

        private android.bluetooth.le.ScanCallback nativeCallback;
        private android.bluetooth.le.ScanSettings nativeSettings;
        private List<android.bluetooth.le.ScanFilter> nativeFilters;

        public ScanCallbackHolder(ScanCallback compatCallback) {
            this.compatCallback = compatCallback;
            this.compatFilters = new ArrayList<>(0);
            createNatives();
        }

        public ScanCallbackHolder(ScanCallback compatCallback, ScanSettings compatSettings, List<ScanFilter> compatFilters) {
            this.compatCallback = compatCallback;
            this.compatSettings = compatSettings;
            this.compatFilters = compatFilters;
            createNatives();
        }

        private void createNatives() {
            nativeCallback = new android.bluetooth.le.ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    int convertedType = LollipopUtils.convertNativeStatusToCompat(callbackType);
                    com.taptrack.tcmptappy.tappyblescanner.ScanResult convertedResult = LollipopUtils.convertNativeResultToCompat(result);

                    compatCallback.onScanResult(convertedType,convertedResult);
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                    compatCallback.onBatchScanResults(LollipopUtils.convertNativeResultListToCompat(results));
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    compatCallback.onScanFailed(LollipopUtils.convertNativeScanFailedErrorCodeToCompat(errorCode));
                }
            };

            this.nativeFilters = LollipopUtils.convertCompatScanFilterListToNative(compatFilters);
            this.nativeSettings = LollipopUtils.convertCompatScanSettingsToNative(compatSettings);
        }

        public android.bluetooth.le.ScanCallback getNativeCallback() {
            return nativeCallback;
        }

        public android.bluetooth.le.ScanSettings getNativeScanSettings() {
            return nativeSettings;
        }

        public List<android.bluetooth.le.ScanFilter> getNativeFilters() {
            return nativeFilters;
        }
    }
}
