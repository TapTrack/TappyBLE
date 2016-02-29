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

package com.taptrack.tcmptappy.tappy.ble.scanner;

import android.bluetooth.BluetoothDevice;

import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleVersionUtils;
import com.taptrack.tcmptappy.tappyblescanner.BluetoothLeScannerCompat;
import com.taptrack.tcmptappy.tappyblescanner.IBluetoothLeScanner;
import com.taptrack.tcmptappy.tappyblescanner.ScanCallback;
import com.taptrack.tcmptappy.tappyblescanner.ScanResult;
import com.taptrack.tcmptappy.tappyblescanner.ScanSettings;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TappyBleScanner {
    private final IBluetoothLeScanner scanner;
    private final Set<TappyBleFoundListener> listeners =
            Collections.newSetFromMap(new ConcurrentHashMap<TappyBleFoundListener, Boolean>(1));

    private ScanSettings settings;
    private AtomicBoolean isScanning = new AtomicBoolean(false);

    private final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            TappyBleDeviceDefinition tappy = TappyBleVersionUtils.getVersionForDevice(device);
            if(tappy != null)
                newTappyFound(tappy);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private TappyBleScanner(IBluetoothLeScanner scanner) {
        this.scanner = scanner;
        this.settings = new ScanSettings.Builder().build();
    }

    public static TappyBleScanner get() {
        return new TappyBleScanner(BluetoothLeScannerCompat.getBluetoothLeScanner());
    }

    public void startScan() {
        if(!isScanning.getAndSet(true)) {
            scanner.startScan(TappyBleVersionUtils.getCompatScanFilter(), settings, callback);
        }
    }

    public void stopScan() {
        if(isScanning.getAndSet(false)) {
            scanner.stopScan(callback);
        }
    }

    protected void newTappyFound(TappyBleDeviceDefinition tappyBle) {
        for(TappyBleFoundListener listener : listeners) {
            listener.onTappyBleFound(tappyBle);
        }
    }

    public void registerTappyBleFoundListener(TappyBleFoundListener listener) {
        listeners.add(listener);
    }

    public void unregisterTappyBleFoundListener(TappyBleFoundListener listener) {
        listeners.remove(listener);
    }

}
