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

package com.kevalpatel2106.robocar.things.motor;

import android.support.annotation.NonNull;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.exception.GpioInitializationException;

import java.io.IOException;

/**
 * Created by Keval Patel on 15/05/17.
 * This class mocks the function of physical D.C. motor.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

abstract class Motor implements AutoCloseable {

    private Gpio mMotorControlPin1;     //Motor control pin 1
    private Gpio mMotorControlPin2;     //Motor control pin 2

    /**
     * Public constructor. This will initialize the motor control {@link Gpio}.
     *
     * @param service {@link PeripheralManagerService}
     */
    Motor(@NonNull PeripheralManagerService service) {
        try {
            mMotorControlPin1 = getControlPin1(service);
            mMotorControlPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            mMotorControlPin2 = getControlPin2(service);
            mMotorControlPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpioInitializationException();
        }
    }

    /**
     * Get {@link Gpio} for motor driver control pin 1.
     *
     * @param service {@link PeripheralManagerService}
     * @return {@link Gpio}
     */
    protected abstract Gpio getControlPin1(PeripheralManagerService service);

    /**
     * Get {@link Gpio} for motor driver control pin 2.
     *
     * @param service {@link PeripheralManagerService}
     * @return {@link Gpio}
     */
    protected abstract Gpio getControlPin2(PeripheralManagerService service);

    /**
     * Rotate motor to forward/clockwise direction.
     */
    public void forward() {
        try {
            mMotorControlPin1.setValue(true);
            mMotorControlPin2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rotate motor to reverse/anti-clockwise direction.
     */
    public void reverse() {
        try {
            mMotorControlPin1.setValue(false);
            mMotorControlPin2.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the motor. (Free spin)
     */
    public void stop() {
        try {
            mMotorControlPin1.setValue(false);
            mMotorControlPin2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Release the motor control.
     *
     * @throws Exception If {@link Gpio} cannot be turnOff.
     */
    @Override
    public void close() throws Exception {
        mMotorControlPin1.close();
        mMotorControlPin2.close();
    }
}