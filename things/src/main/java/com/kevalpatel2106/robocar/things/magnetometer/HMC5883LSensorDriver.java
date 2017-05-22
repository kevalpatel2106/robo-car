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

import android.hardware.Sensor;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.UserSensor;
import com.google.android.things.userdriver.UserSensorDriver;
import com.google.android.things.userdriver.UserSensorReading;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by cagdas on 24.12.2016.
 */

final class HMC5883LSensorDriver implements AutoCloseable {

    private static final String DRIVER_VENDOR = "";
    private static final String DRIVER_NAME = "HMCL5883L";
    private static final int DRIVER_VERSION = 1;
    private static final int DRIVER_RESOLUTION = 8; //±8 Gauss
    private static final float DRIVER_POWER = 3.6f; // Volt
    private static final int DRIVER_MAX_DELAY_US = Math.round(1000000.f / 0.75f);
    private static final int DRIVER_MIN_DELAY_US = Math.round(1000000.f / 75f);
    private static final String DRIVER_REQUIRED_PERMISSION = "";

    private static final String TAG = HMC5883LSensorDriver.class.getName();

    private HMC5883LDriver hmcl5883l;
    private MagnetometerUserDriver mUserDriver;

    public HMC5883LSensorDriver(String bus) throws IOException {
        hmcl5883l = new HMC5883LDriver(bus);
    }

    void registerMagmetormeterSensor() {
        if (hmcl5883l == null) {
            throw new IllegalStateException("cannot register closed driver");
        }

        if (mUserDriver == null) {
            mUserDriver = new MagnetometerUserDriver();
            UserDriverManager.getManager().registerSensor(mUserDriver.getUserSensor());
        }
    }

    void unregisterMagmetormeterSensor() {
        if (mUserDriver != null) {
            UserDriverManager.getManager().unregisterSensor(mUserDriver.getUserSensor());
            mUserDriver = null;
        }
    }

    @Override
    public void close() throws Exception {
        unregisterMagmetormeterSensor();
        if (hmcl5883l != null) {
            try {
                hmcl5883l.close();
            } finally {
                hmcl5883l = null;
            }
        }
    }

    private class MagnetometerUserDriver extends UserSensorDriver {

        private UserSensor mUserSensor;

        private UserSensor getUserSensor() {
            if (mUserSensor == null) {
                mUserSensor = UserSensor.builder()
                        .setType(Sensor.TYPE_MAGNETIC_FIELD)
                        .setName(DRIVER_NAME)
                        .setVendor(DRIVER_VENDOR)
                        .setVersion(DRIVER_VERSION)
                        .setResolution(DRIVER_RESOLUTION)
                        .setPower(DRIVER_POWER)
                        .setMinDelay(DRIVER_MIN_DELAY_US)
                        .setRequiredPermission(DRIVER_REQUIRED_PERMISSION)
                        .setMaxDelay(DRIVER_MAX_DELAY_US)
                        .setUuid(UUID.randomUUID())
                        .setDriver(this)
                        .build();
            }
            return mUserSensor;
        }

        @Override
        public UserSensorReading read() throws IOException {
            return new UserSensorReading(hmcl5883l.getMagnitudes());
        }
    }
}
