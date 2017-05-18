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

package com.kevalpatel2106.robocar.things.chassis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.beacon.Beacon;
import com.kevalpatel2106.robocar.things.camera.Camera;
import com.kevalpatel2106.robocar.things.camera.CameraCaptureListener;
import com.kevalpatel2106.robocar.things.display.LcdDisplay;
import com.kevalpatel2106.robocar.things.magnetometer.MagnetoMeter;
import com.kevalpatel2106.robocar.things.motor.LeftMotor;
import com.kevalpatel2106.robocar.things.motor.RightMotor;
import com.kevalpatel2106.robocar.things.radar.FrontRadar;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;

/**
 * Created by Keval on 15-May-17.
 * This class represents the chassis of the robot car.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class Chassis extends ChassisMock {
    private Builder mBuilder;

    /**
     * Public constructor.
     */
    private Chassis(Builder builder) {
        mBuilder = builder;
    }

    /**
     * @return {@link FrontRadar}
     */
    @Nullable
    @Override
    public FrontRadar getFrontRadar() {
        return mBuilder.mFrontRadar;
    }

    /**
     * @return {@link RightMotor}
     */
    @NonNull
    @Override
    public RightMotor getRightMotor() {
        return mBuilder.mRightMotor;
    }

    /**
     * @return {@link LeftMotor}
     */
    @NonNull
    @Override
    public LeftMotor getLeftMotor() {
        return mBuilder.mLeftMotor;
    }

    /**
     * @return {@link Beacon}
     */
    @Nullable
    @Override
    public Beacon getBeacon() {
        return mBuilder.mBeacon;
    }

    /**
     * @return {@link LcdDisplay}
     */
    @Nullable
    @Override
    public LcdDisplay getLcdDisplay() {
        return mBuilder.mLcdDisplay;
    }

    /**
     * @return {@link Camera}
     */
    @Nullable
    @Override
    public Camera getCamera() {
        return mBuilder.mCamera;
    }

    /**
     * @return {@link com.kevalpatel2106.robocar.things.magnetometer.MagnetoMeter}
     */
    @Nullable
    @Override
    public MagnetoMeter getMagnetoMeter() {
        return mBuilder.mMagnetoMeter;
    }

    /**
     * Take a left turn. This method is for internal use only.
     */
    public void turnLeftInternal() {
        mBuilder.mLeftMotor.reverse();
        mBuilder.mRightMotor.forward();
    }

    /**
     * Take a right turn. This method is for internal use only.
     */
    public void turnRightInternal() {
        mBuilder.mLeftMotor.forward();
        mBuilder.mRightMotor.reverse();
    }

    /**
     * Move forward. This method is for internal use only.
     */
    public void moveForwardInternal() {
        mBuilder.mLeftMotor.reverse();
        mBuilder.mRightMotor.reverse();
    }

    /**
     * Move reverse. This method is for internal use only.
     */
    public void moveReverseInternal() {
        mBuilder.mLeftMotor.forward();
        mBuilder.mRightMotor.forward();
    }

    /**
     * Stop the motor. This method is for internal use only.
     */
    public void stopInternal() {
        mBuilder.mLeftMotor.stop();
        mBuilder.mRightMotor.stop();
    }

    /**
     * Turn off the display and release the resources.
     *
     * @throws Exception If releasing resources fail.
     */
    @Override
    public void turnOff() throws Exception {
        mBuilder.mLeftMotor.close();
        mBuilder.mRightMotor.close();

        if (mBuilder.mFrontRadar != null) mBuilder.mFrontRadar.turnOff();
        if (mBuilder.mBeacon != null) mBuilder.mBeacon.stopTransmission();
        if (mBuilder.mLcdDisplay != null) mBuilder.mLcdDisplay.turnOff();
        if (mBuilder.mCamera != null) mBuilder.mCamera.turnOff();
        if (mBuilder.mMagnetoMeter != null) mBuilder.mMagnetoMeter.turnOff();
    }

    /**
     * Chassis builder. Mount your component to the chassis here.
     */
    public static class Builder extends ChassisMock.BuilderMock {

        @Nullable
        private FrontRadar mFrontRadar;     //Front radar

        @SuppressWarnings("NullableProblems")
        @NonNull
        private RightMotor mRightMotor;     //Right motor.

        @SuppressWarnings("NullableProblems")
        @NonNull
        private LeftMotor mLeftMotor;       //Left side motor

        @Nullable
        private Beacon mBeacon;             //Alt beacon.

        @Nullable
        private LcdDisplay mLcdDisplay;     //Front display

        @Nullable
        private Camera mCamera;

        @Nullable
        private MagnetoMeter mMagnetoMeter; //Magnetometer for getting the direction

        public Builder() {
            //Do nothing
        }

        /**
         * Mount the front radar.
         */
        @Override
        public Builder mountFrontRadar(@NonNull ObstacleAlertListener listener) {
            mFrontRadar = new FrontRadar(listener);
            mFrontRadar.startTransmission();
            return this;
        }

        /**
         * Mount the left side motor.
         */
        @Override
        public Builder mountLeftMotor(@NonNull PeripheralManagerService service) {
            mLeftMotor = new LeftMotor(service);
            return this;
        }

        /**
         * Mount the right side motor.
         */
        @Override
        public Builder mountRightMotor(@NonNull PeripheralManagerService service) {
            mRightMotor = new RightMotor(service);
            return this;
        }

        /**
         * Mount the beacon.
         */
        @Override
        public Builder mountBeacon(@NonNull Context context) {
            mBeacon = new Beacon(context);
            mBeacon.startTransmission();
            return this;
        }

        /**
         * Mount the display.
         */
        @Override
        public Builder mountDisplay() {
            mLcdDisplay = new LcdDisplay();
            mLcdDisplay.turnOn();
            return this;
        }

        @Override
        public Builder mountCamera(@NonNull Context context,
                                   @NonNull CameraCaptureListener listener) {
            mCamera = new Camera(context, listener);
            mCamera.turnOn();
            return this;
        }

        @Override
        public Builder mountMagnetometer() {
            mMagnetoMeter = new MagnetoMeter();
            mMagnetoMeter.turnOn();
            return this;
        }

        /**
         * Validate and initialize the components.
         *
         * @return {@link Chassis}
         */
        @SuppressWarnings("ConstantConditions")
        @Override
        public Chassis build() {
            if (mLeftMotor == null)
                throw new IllegalArgumentException("Cannot set left motor to null. Are you building car?");
            if (mRightMotor == null)
                throw new IllegalArgumentException("Cannot set right motor to null. Are you building car?");
            return new Chassis(this);
        }
    }
}
