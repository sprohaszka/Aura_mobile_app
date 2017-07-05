/**
 * @file GattDescriptorReadOperation.java
 * @author  NordicSemiconductor
 * @version 1.0
 *
 *
 * @section LICENSE
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @section DESCRIPTION
 *
 */
package com.wearablesensor.aura.device_pairing.bluetooth.gatt.operations;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.wearablesensor.aura.device_pairing.bluetooth.gatt.GattDescriptorReadCallback;

import java.util.UUID;


public class GattDescriptorReadOperation extends GattOperation {
    private final String TAG = this.getClass().getSimpleName();

    private final UUID mService;
    private final UUID mCharacteristic;
    private final UUID mDescriptor;
    private final GattDescriptorReadCallback mCallback;

    public GattDescriptorReadOperation(BluetoothDevice device, UUID service, UUID characteristic, UUID descriptor, GattDescriptorReadCallback callback) {
        super(device);
        mService = service;
        mCharacteristic = characteristic;
        mDescriptor = descriptor;
        mCallback = callback;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Log.d(TAG, "Reading from " + mDescriptor);
        BluetoothGattDescriptor descriptor = gatt.getService(mService).getCharacteristic(mCharacteristic).getDescriptor(mDescriptor);
        gatt.readDescriptor(descriptor);
    }

    @Override
    public boolean hasAvailableCompletionCallback() {
        return true;
    }

    public void onRead(BluetoothGattDescriptor descriptor) {
        mCallback.call(descriptor.getValue());
    }
}