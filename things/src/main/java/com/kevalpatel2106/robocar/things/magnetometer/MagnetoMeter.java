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

package com.kevalpatel2106.robocar.things.magnetometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.kevalpatel2106.robocar.things.processor.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval Patel on 17/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class MagnetoMeter extends MagnetometerMock {
    private static final String TAG = MagnetoMeter.class.getSimpleName();

    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "Mag X " + event.values[0]);
            Log.d(TAG, "Mag Y " + event.values[1]);
            Log.d(TAG, "Mag Z " + event.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private SensorManager mSensorManager;
    private HMC5883LSensorDriver mSensorDriver;

    @Override
    public void turnOn(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerDynamicSensorCallback(new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(Sensor sensor) {
                if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    mSensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        });

        try {
            mSensorDriver = new HMC5883LSensorDriver(BoardDefaults.getI2CPortForMagnetometer());
            mSensorDriver.registerMagmetormeterSensor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void turnOff() {
        if (mSensorDriver != null) {
            mSensorManager.unregisterListener(mListener);
            mSensorDriver.unregisterMagmetormeterSensor();
            try {
                mSensorDriver.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing sensor", e);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mSensorDriver = null;
            }
        }
    }
}
