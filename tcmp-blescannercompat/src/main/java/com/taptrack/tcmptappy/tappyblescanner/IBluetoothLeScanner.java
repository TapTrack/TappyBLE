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

package com.taptrack.tcmptappy.tappyblescanner;

import android.bluetooth.BluetoothAdapter;
import android.support.annotation.NonNull;

import java.util.List;

    /**
     * This class provides methods to perform scan related operations for Bluetooth LE devices. An
     * application can scan for a particular type of Bluetooth LE devices using {@link ScanFilter}. It
     * can also request different types of callbacks for delivering the result.
     * <p>
     * Use {@link BluetoothAdapter#getBluetoothLeScanner()} to get an instance of
     * {@link IBluetoothLeScanner}.
     * <p>
     * <b>Note:</b> Most of the scan methods here require
     * {@link android.Manifest.permission#BLUETOOTH_ADMIN} permission.
     *
     * @see ScanFilter
     */
    public interface IBluetoothLeScanner {
        /**
         * Start Bluetooth LE scan with default parameters and no filters. The scan results will be
         * delivered through {@code callback}.
         * <p>
         * Requires {@link android.Manifest.permission#BLUETOOTH_ADMIN} permission.
         *
         * @param callback Callback used to deliver scan results.
         * @throws IllegalArgumentException If {@code callback} is null.
         */

        public void startScan(@NonNull final ScanCallback callback);
        /**
         * Start Bluetooth LE scan. The scan results will be delivered through {@code callback}.
         * <p>
         * Requires {@link android.Manifest.permission#BLUETOOTH_ADMIN} permission.
         *
         * @param filters {@link ScanFilter}s for finding exact BLE devices.
         * @param settings Settings for the scan.
         * @param callback Callback used to deliver scan results.
         * @throws IllegalArgumentException If {@code settings} or {@code callback} is null.
         */
        public void startScan(List<ScanFilter> filters, @NonNull ScanSettings settings,
                              @NonNull  final ScanCallback callback);

        /**
         * Stops an ongoing Bluetooth LE scan.
         * <p>
         * Requires {@link android.Manifest.permission#BLUETOOTH_ADMIN} permission.
         *
         * @param callback
         */
        public void stopScan(@NonNull ScanCallback callback);
        /**
         * Flush pending batch scan results stored in Bluetooth controller. This will return Bluetooth
         * LE scan results batched on bluetooth controller. Returns immediately, batch scan results data
         * will be delivered through the {@code callback}.
         *
         * @param callback Callback of the Bluetooth LE Scan, it has to be the same instance as the one
         *            used to start scan.
         */
        public void flushPendingScanResults(@NonNull ScanCallback callback);

}
