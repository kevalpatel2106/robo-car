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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kevalpatel2106.robocar.things.beacon.Beacon;
import com.kevalpatel2106.robocar.things.motor.LeftMotor;
import com.kevalpatel2106.robocar.things.motor.RightMotor;
import com.kevalpatel2106.robocar.things.radar.FrontRadar;

/**
 * Created by Keval on 15-May-17.
 * This is the mock representation for the robot chassis. This will mount all the components you
 * mounted physically on the chassis. There are abstract methods to mount each devices.
 * <p>
 * You can mount:
 * <li>Beacon - Optional</li>
 * <li>Motors - Required</li>
 * <li>Front radar - Optional</li>
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

abstract class ChassisMock implements AutoCloseable {

    @Nullable
    private FrontRadar mFrontRadar;   //Hcsr04 at the front of the car

    @SuppressWarnings("NullableProblems")
    @NonNull
    private RightMotor mRightMotor;   //Right motor.

    @SuppressWarnings("NullableProblems")
    @NonNull
    private LeftMotor mLeftMotor;     //Left side motor

    @Nullable
    private Beacon mBeacon;           //Alt beacon.

    /**
     * Public constructor.
     */
    ChassisMock() {
    }

    /**
     * Mount the component and build the chassis.
     */
    @SuppressWarnings("ConstantConditions")
    protected void build() {
        mFrontRadar = mountFrontRadar();
        mBeacon = mountBeacon();

        mLeftMotor = mountLeftMotor();
        if (mLeftMotor == null)
            throw new IllegalArgumentException("Cannot set left motor to null. Are you building car?");

        mRightMotor = mountRightMotor();
        if (mRightMotor == null)
            throw new IllegalArgumentException("Cannot set right motor to null. Are you building car?");

        //Initialize the components.
        init();
    }

    /**
     * This is an abstract method to initialize the components. (e.g. Start radar, start beacon etc)
     * This method will be called after the chassis is built.
     *
     * @see #build()
     */
    protected abstract void init();

    /**
     * Mount the front radar. If your car does not have front radar pass null.
     *
     * @return {@link FrontRadar}
     */
    @Nullable
    protected abstract FrontRadar mountFrontRadar();

    /**
     * Mount the left side motor. It's value cannot be null.
     *
     * @return {@link LeftMotor}
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    protected abstract LeftMotor mountLeftMotor();

    /**
     * Mount the right side motor. It's value cannot be null.
     *
     * @return {@link RightMotor}
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    protected abstract RightMotor mountRightMotor();

    /**
     * Mount the beacon. If your car does not have beacon pass null.
     *
     * @return {@link Beacon}
     */
    @Nullable
    protected abstract Beacon mountBeacon();

    @Nullable
    FrontRadar getFrontRadar() {
        return mFrontRadar;
    }

    @NonNull
    RightMotor getRightMotor() {
        return mRightMotor;
    }

    @NonNull
    LeftMotor getLeftMotor() {
        return mLeftMotor;
    }

    @Nullable
    Beacon getBeacon() {
        return mBeacon;
    }
}
