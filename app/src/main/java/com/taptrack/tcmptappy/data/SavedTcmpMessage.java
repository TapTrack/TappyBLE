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

import java.util.Arrays;

public class SavedTcmpMessage {
    private Long dbId;

    private String name;
    private String address;

    private Long timestamp;
    private byte[] message;

    public SavedTcmpMessage(Long dbId, String name, String address, Long timestamp, byte[] message) {
        this.dbId = dbId;
        this.name = name;
        this.address = address;
        this.timestamp = timestamp;
        this.message = message;
    }

    public SavedTcmpMessage(String name, String address, Long timestamp, byte[] message) {
        this.name = name;
        this.address = address;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Long getDbId() {
        return dbId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public byte[] getMessage() {
        return message;
    }

    public boolean isFromMe() {
        return name.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedTcmpMessage that = (SavedTcmpMessage) o;

        if (getDbId() != null ? !getDbId().equals(that.getDbId()) : that.getDbId() != null)
            return false;
        if (!getName().equals(that.getName())) return false;
        if (!getAddress().equals(that.getAddress())) return false;
        if (!getTimestamp().equals(that.getTimestamp())) return false;
        return Arrays.equals(getMessage(), that.getMessage());

    }

    @Override
    public int hashCode() {
        int result = getDbId() != null ? getDbId().hashCode() : 0;
        if(result > 0)
            return result;
        result = 31 * result + getName().hashCode();
        result = 31 * result + getAddress().hashCode();
        result = 31 * result + getTimestamp().hashCode();
        result = 31 * result + Arrays.hashCode(getMessage());
        return result;
    }
}
