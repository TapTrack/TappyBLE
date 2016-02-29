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

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import com.taptrack.tcmptappy.tappyblescanner.ScanCallback;
import com.taptrack.tcmptappy.tappyblescanner.ScanRecord;
import com.taptrack.tcmptappy.tappyblescanner.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class LollipopUtils {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int convertNativeStatusToCompat(int nativeStatus) {
        switch (nativeStatus) {
            case ScanSettings.CALLBACK_TYPE_FIRST_MATCH:
                return com.taptrack.tcmptappy.tappyblescanner.ScanSettings.CALLBACK_TYPE_FIRST_MATCH;
            case ScanSettings.CALLBACK_TYPE_MATCH_LOST:
                return com.taptrack.tcmptappy.tappyblescanner.ScanSettings.CALLBACK_TYPE_MATCH_LOST;
            default:
            case ScanSettings.CALLBACK_TYPE_ALL_MATCHES:
                return com.taptrack.tcmptappy.tappyblescanner.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScanResult convertNativeResultToCompat(android.bluetooth.le.ScanResult nativeResult) {
        BluetoothDevice device = nativeResult.getDevice();
        int rssi = nativeResult.getRssi();
        long timestampNanos = nativeResult.getTimestampNanos();
        ScanRecord compatRecord = convertNativeRecordToCompat(nativeResult.getScanRecord());

        return new ScanResult(device,compatRecord,rssi,timestampNanos);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScanRecord convertNativeRecordToCompat(android.bluetooth.le.ScanRecord nativeRecord) {
        return ScanRecord.parseFromBytes(nativeRecord.getBytes());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static List<ScanResult> convertNativeResultListToCompat(List<android.bluetooth.le.ScanResult> nativeResults) {
        List<ScanResult> compatResults = new ArrayList<>(nativeResults.size());
        for(android.bluetooth.le.ScanResult nativeResult : nativeResults) {
            compatResults.add(convertNativeResultToCompat(nativeResult));
        }
        return compatResults;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int convertNativeScanFailedErrorCodeToCompat(int nativeCode) {
        switch(nativeCode) {
            case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                return android.bluetooth.le.ScanCallback.SCAN_FAILED_ALREADY_STARTED;
            case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                return android.bluetooth.le.ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED;
            case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                return android.bluetooth.le.ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED;
            case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
            default:
                return android.bluetooth.le.ScanCallback.SCAN_FAILED_INTERNAL_ERROR;
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScanSettings convertCompatScanSettingsToNative(com.taptrack.tcmptappy.tappyblescanner.ScanSettings compatSettings) {
        if(compatSettings == null)
            return null;

        ScanSettings.Builder nativeBuilder = new ScanSettings.Builder();
        nativeBuilder = nativeBuilder.setReportDelay(compatSettings.getReportDelayMillis());

        switch (compatSettings.getScanMode()) {
            case com.taptrack.tcmptappy.tappyblescanner.ScanSettings.SCAN_MODE_BALANCED: {
                nativeBuilder = nativeBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
                break;
            }
            case com.taptrack.tcmptappy.tappyblescanner.ScanSettings.SCAN_MODE_LOW_LATENCY: {
                nativeBuilder = nativeBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
                break;
            }
            default:
            case com.taptrack.tcmptappy.tappyblescanner.ScanSettings.SCAN_MODE_LOW_POWER: {
                nativeBuilder = nativeBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
                break;
            }
        }
//          Only in marshmallow
//        switch (compatSettings.getCallbackType()) {
//            case ScanSettings.CALLBACK_TYPE_MATCH_LOST: {
//                nativeBuilder = nativeBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_MATCH_LOST);
//                break;
//            }
//            case ScanSettings.CALLBACK_TYPE_FIRST_MATCH: {
//                nativeBuilder = nativeBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH);
//                break;
//            }
//            default:
//            case ScanSettings.CALLBACK_TYPE_ALL_MATCHES: {
//                nativeBuilder = nativeBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//                break;
//            }
//        }


        return nativeBuilder.build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static List<ScanFilter> convertCompatScanFilterListToNative(List<com.taptrack.tcmptappy.tappyblescanner.ScanFilter> compatFilters) {
        if(compatFilters == null)
            return null;
        List<ScanFilter> nativeFilters = new ArrayList<>(compatFilters.size());
        for (com.taptrack.tcmptappy.tappyblescanner.ScanFilter compatFilter :
                compatFilters) {
            nativeFilters.add(convertCompatScanFilterToNative(compatFilter));
        }
        return nativeFilters;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ScanFilter convertCompatScanFilterToNative(com.taptrack.tcmptappy.tappyblescanner.ScanFilter compatFilter) {
        if(compatFilter == null)
            return null;

        ScanFilter.Builder nativeBuilder = new ScanFilter.Builder();

        if(compatFilter.getDeviceAddress() != null)
            nativeBuilder = nativeBuilder.setDeviceAddress(compatFilter.getDeviceAddress());

        if(compatFilter.getDeviceName() != null)
            nativeBuilder = nativeBuilder.setDeviceAddress(compatFilter.getDeviceName());

        if(compatFilter.getManufacturerData() != null && compatFilter.getManufacturerId() != -1) {
            if(compatFilter.getManufacturerDataMask() != null) {
                nativeBuilder = nativeBuilder.setManufacturerData(compatFilter.getManufacturerId(),
                        compatFilter.getManufacturerData(),
                        compatFilter.getManufacturerDataMask());
            }
            else {
                nativeBuilder = nativeBuilder.setManufacturerData(compatFilter.getManufacturerId(),
                        compatFilter.getManufacturerData());
            }
        }


        if(compatFilter.getServiceDataUuid() != null && compatFilter.getServiceData() != null) {
            if(compatFilter.getServiceDataMask() != null) {
                nativeBuilder = nativeBuilder.setServiceData(compatFilter.getServiceDataUuid(),
                        compatFilter.getServiceData(),
                        compatFilter.getServiceDataMask());
            }
            else {
                nativeBuilder = nativeBuilder.setServiceData(compatFilter.getServiceDataUuid(),
                        compatFilter.getServiceData());
            }
        }

        if(compatFilter.getServiceUuid() != null) {
            if(compatFilter.getServiceUuidMask() != null)
                nativeBuilder = nativeBuilder.setServiceUuid(compatFilter.getServiceUuid(),compatFilter.getServiceUuidMask());
            else
                nativeBuilder = nativeBuilder.setServiceUuid(compatFilter.getServiceUuid());
        }

        return nativeBuilder.build();
    }
}
