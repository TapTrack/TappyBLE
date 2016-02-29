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

package com.taptrack.tcmptappy.tappy.ble;

public abstract class AbstractTappyBleDeviceDefinition implements TappyBleDeviceDefinition {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TappyBleDeviceDefinition that = (TappyBleDeviceDefinition) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getAddress() != null ? !getAddress().equals(that.getAddress()) : that.getAddress() != null)
            return false;
        if (getSerialServiceUuid() != null ? !getSerialServiceUuid().equals(that.getSerialServiceUuid()) : that.getSerialServiceUuid() != null)
            return false;
        if (getRxCharacteristicUuid() != null ? !getRxCharacteristicUuid().equals(that.getRxCharacteristicUuid()) : that.getRxCharacteristicUuid() != null)
            return false;
        return !(getTxCharacteristicUuid() != null ? !getTxCharacteristicUuid().equals(that.getTxCharacteristicUuid()) : that.getTxCharacteristicUuid() != null);

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getSerialServiceUuid() != null ? getSerialServiceUuid().hashCode() : 0);
        result = 31 * result + (getRxCharacteristicUuid() != null ? getRxCharacteristicUuid().hashCode() : 0);
        result = 31 * result + (getTxCharacteristicUuid() != null ? getTxCharacteristicUuid().hashCode() : 0);
        return result;
    }
}
