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

package com.taptrack.tcmptappy.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.taptrack.tappyble.R;

public class MarshmallowCompatBlePermDelegate {
    protected Activity activity;
    protected boolean hasCoarsePermission = false;
    protected BluetoothAdapter bluetoothAdapter;

    protected static final int COARSE_REQUEST_CODE = 307;
    protected static final int REQUEST_ENABLE_BT = 308;

    public MarshmallowCompatBlePermDelegate(Activity activity) {
        this.activity = activity;
    }

    public void onCreate() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, R.string.bluetooth_must_be_supported, Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            hasCoarsePermission = true;
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                activity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        COARSE_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == COARSE_REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasCoarsePermission = true;
            }
            else {
                //TODO: Make this less fire and brimstone
                Toast.makeText(activity, R.string.rationale_ble_coarse_location, Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(activity, R.string.bluetooth_must_be_enabled, Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }
    }

    public void onResume() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
