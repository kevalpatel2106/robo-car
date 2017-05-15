/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.robocar.things.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Collections;

/**
 * Created by Keval Patel on 14/05/17.
 * Class that mocks physical beacon. This uses Raspberry Pi bluetooth and convert it to an alt beacon.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 * @see 'https://github.com/AltBeacon/altbeacon-transmitter-android'
 */

public final class Beacon {
    private static final String TAG = Beacon.class.getSimpleName();

    private static final String BEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static final String BEACON_ID_1 = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";
    private static final String BEACON_ID_2 = "1";
    private static final String BEACON_ID_3 = "2";
    private static BeaconTransmitter mBeaconTransmitter;    //Beacon transistor
    @NonNull
    private final Context mContext;

    /**
     * Public constructor.
     *
     * @param context instance of the activity.
     */
    public Beacon(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Start transmitting as beacon.
     */
    public void initBeaconTransmission() {
        if (checkPrerequisites()) {
            // Sets up to transmit as an AltBeacon-style beacon.  If you wish to transmit as a different
            // type of beacon, simply provide a different parser expression.  To find other parser expressions,
            // for other beacon types, do a Google search for "setBeaconLayout" including the quotes
            mBeaconTransmitter = new org.altbeacon.beacon.BeaconTransmitter(mContext,
                    new BeaconParser().setBeaconLayout(BEACON_LAYOUT));

            // Transmit a beacon with Identifiers 2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6 1 2
            org.altbeacon.beacon.Beacon beacon = new org.altbeacon.beacon.Beacon.Builder()
                    .setId1(BEACON_ID_1)
                    .setId2(BEACON_ID_2)
                    .setId3(BEACON_ID_3)
                    .setManufacturer(0x0000) // Choose a number of 0x00ff or less as some devices cannot detect beacons with a manufacturer code > 0x00ff
                    .setTxPower(-59)
                    .setDataFields(Collections.singletonList(0L))
                    .build();

            mBeaconTransmitter.startAdvertising(beacon);
        }
    }

    /**
     * Stop beacon transmission.
     */
    public void stopBeaconTransmission() {
        mBeaconTransmitter.stopAdvertising();
    }

    /**
     * Check if the current hardware can transmit as beacon?
     *
     * @return true if the hardware can convert to beacon.
     */
    private boolean checkPrerequisites() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "checkPrerequisites: Bluetooth LE not supported by this device");
            return false;
        }

        //Enable bluetooth
        enableBluetooth();

        try {
            // Check to see if the getBluetoothLeAdvertiser is available.
            // If not, this will throw an exception indicating we are not running Android L
            ((BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().getBluetoothLeAdvertiser();
        } catch (Exception e) {
            Log.e(TAG, "checkPrerequisites: Bluetooth LE advertising unavailable");
            return false;

        }
        return true;
    }

    /**
     * Turn on the bluetooth if it is not already turned on.
     */
    private void enableBluetooth() {
        BluetoothAdapter adapter = ((BluetoothManager) mContext
                .getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        if (!adapter.isEnabled()) adapter.enable();
    }
}
