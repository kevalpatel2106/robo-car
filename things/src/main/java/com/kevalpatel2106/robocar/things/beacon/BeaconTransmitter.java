package com.kevalpatel2106.robocar.things.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;

import java.util.Collections;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class BeaconTransmitter {

    private static final String TAG = BeaconTransmitter.class.getSimpleName();
    private static org.altbeacon.beacon.BeaconTransmitter mBeaconTransmitter;
    private final Context mContext;

    public BeaconTransmitter(Context context) {
        mContext = context;
    }

    public void initBeaconTransmission() {
        if (checkPrerequisites(mContext)) {

            // Sets up to transmit as an AltBeacon-style beacon.  If you wish to transmit as a different
            // type of beacon, simply provide a different parser expression.  To find other parser expressions,
            // for other beacon types, do a Google search for "setBeaconLayout" including the quotes
            mBeaconTransmitter = new org.altbeacon.beacon.BeaconTransmitter(mContext,
                    new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

            // Transmit a beacon with Identifiers 2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6 1 2
            Beacon beacon = new Beacon.Builder()
                    .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
                    .setId2("1")
                    .setId3("2")
                    .setManufacturer(0x0000) // Choose a number of 0x00ff or less as some devices cannot detect beacons with a manufacturer code > 0x00ff
                    .setTxPower(-59)
                    .setDataFields(Collections.singletonList(0L))
                    .build();

            mBeaconTransmitter.startAdvertising(beacon);
        }

    }

    public void stopBeaconTransmission() {
        mBeaconTransmitter.stopAdvertising();
    }

    private boolean checkPrerequisites(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAG, "checkPrerequisites: Bluetooth LE not supported by this device");
            return false;
        }

        //Enable bluetooth
        enableBluetooth();

        try {
            // Check to see if the getBluetoothLeAdvertiser is available.
            // If not, this will throw an exception indicating we are not running Android L
            ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().getBluetoothLeAdvertiser();
        } catch (Exception e) {
            Log.e(TAG, "checkPrerequisites: Bluetooth LE advertising unavailable");
            return false;

        }
        return true;
    }

    private void enableBluetooth() {
        BluetoothAdapter adapter = ((BluetoothManager) mContext
                .getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        if (!adapter.isEnabled()) adapter.enable();
    }
}
