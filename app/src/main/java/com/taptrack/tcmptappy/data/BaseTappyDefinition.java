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

package com.taptrack.tcmptappy.data;

import com.taptrack.tcmptappy.tappy.ble.AbstractTappyBleDeviceDefinition;
import com.taptrack.tcmptappy.tappy.ble.TappyBleDeviceDefinition;

import java.util.UUID;

class BaseTappyDefinition extends AbstractTappyBleDeviceDefinition implements TappyBleDeviceDefinition {
    private String name;
    private String address;
    private UUID serviceUuid;
    private UUID rxCharacteristicUuid;
    private UUID txCharacteristicUuid;

    public BaseTappyDefinition(String name,
                               String address,
                               UUID serviceUuid,
                               UUID rxCharacteristicUuid,
                               UUID txCharacteristicUuid) {
        this.name = name;
        this.address = address;
        this.serviceUuid = serviceUuid;
        this.rxCharacteristicUuid = rxCharacteristicUuid;
        this.txCharacteristicUuid = txCharacteristicUuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public UUID getSerialServiceUuid() {
        return serviceUuid;
    }

    @Override
    public UUID getTxCharacteristicUuid() {
        return txCharacteristicUuid;
    }

    @Override
    public UUID getRxCharacteristicUuid() {
        return rxCharacteristicUuid;
    }
}
