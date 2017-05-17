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
import com.kevalpatel2106.robocar.things.display.LcdDisplay;
import com.kevalpatel2106.robocar.things.motor.LeftMotor;
import com.kevalpatel2106.robocar.things.motor.RightMotor;
import com.kevalpatel2106.robocar.things.radar.FrontRadar;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;

/**
 * Created by Keval on 15-May-17.
 * This is the mock representation for the robot chassis. This will mount all the components you
 * mounted physically on the chassis. There are abstract methods to mount each devices.
 * <p>
 * You can mount:
 * <li>Beacon - Optional</li>
 * <li>Motors - Required</li>
 * <li>Front radar - Optional</li>
 * <li>LcdDisplay - Optional</li>
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

abstract class ChassisMock {
    /**
     * Public constructor.
     */
    ChassisMock() {
        //Do nothing
    }

    /**
     * Turn off the components on chassis.
     */
    public abstract void turnOff() throws Exception;

    /**
     * @return {@link FrontRadar}
     */
    @Nullable
    public abstract FrontRadar getFrontRadar();

    /**
     * @return {@link RightMotor}
     */
    @NonNull
    public abstract RightMotor getRightMotor();

    /**
     * @return {@link LeftMotor}
     */
    @NonNull
    public abstract LeftMotor getLeftMotor();

    /**
     * @return {@link Beacon}
     */
    @Nullable
    public abstract Beacon getBeacon();

    /**
     * @return {@link LcdDisplay}
     */
    @Nullable
    public abstract LcdDisplay getLcdDisplay();

    @SuppressWarnings("WeakerAccess")
    protected static abstract class BuilderMock {
        /**
         * Mount the front radar. This is an optional component.
         */
        public abstract BuilderMock mountFrontRadar(@NonNull ObstacleAlertListener listener);

        /**
         * Mount the left side motor.
         */
        @SuppressWarnings("NullableProblems")
        public abstract BuilderMock mountLeftMotor(@NonNull PeripheralManagerService service);

        /**
         * Mount the right side motor.
         */
        @SuppressWarnings("NullableProblems")
        public abstract BuilderMock mountRightMotor(@NonNull PeripheralManagerService service);

        /**
         * Mount the beacon. This is an optional component.
         */
        public abstract BuilderMock mountBeacon(@NonNull Context context);

        /**
         * Mount the front display. This is an optional component.
         */
        public abstract BuilderMock mountDisplay();

        /**
         * Validate and initialize the components.
         *
         * @return {@link Chassis}
         */
        public abstract ChassisMock build();
    }
}
