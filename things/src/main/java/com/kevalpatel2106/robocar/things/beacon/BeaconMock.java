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

/**
 * Created by Keval Patel on 17/05/17.
 * Mock representation of the physical alt beacon.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

abstract class BeaconMock {
    /**
     * Layout string for the ALT beacon.
     */
    static final String ALT_BEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";

    /**
     * Start transmitting as beacon.
     */
    public abstract void startTransmission();

    /**
     * Stop beacon transmission.
     */
    public abstract void stopTransmission();

    /**
     * Check if the current hardware can transmit as beacon?
     *
     * @return true if the hardware can convert to beacon.
     */
    protected abstract boolean checkPrerequisites();

    /**
     * Check if the beacon is currently transmitting or not?
     *
     * @return true if the beacon is currently transmitting.
     */
    public abstract boolean isTransmitting();
}
