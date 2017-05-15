/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.robocar.things.processor;

import android.os.Build;

import com.google.android.things.pio.PeripheralManagerService;

import java.util.List;

/**
 * Class to map different GPIO pins to hardware components.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */
@SuppressWarnings("WeakerAccess")
public final class BoardDefaults {
    private static final String DEVICE_EDISON_ARDUINO = "edison_arduino";
    private static final String DEVICE_EDISON = "edison";
    private static final String DEVICE_JOULE = "joule";
    private static final String DEVICE_RPI3 = "rpi3";
    private static final String DEVICE_PICO = "imx6ul_pico";
    private static final String DEVICE_VVDN = "imx6ul_iopb";
    private static String sBoardVariant = "";

    /**
     * Return the GPIO pin for control 1 of the right side motor.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForIn1() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM6";
            default:
                throw new IllegalStateException("Unknown board " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for control 2 of the right side motor.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForIn2() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM5";
            default:
                throw new IllegalStateException("Unknown board " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for control 1 of the left side motor.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForIn3() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM22";
            default:
                throw new IllegalStateException("Unknown board " + Build.DEVICE);
        }
    }

    /**
     * Return the GPIO pin for control 2 of the left side motor.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForIn4() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM27";
            default:
                throw new IllegalStateException("Unknown board " + Build.DEVICE);
        }
    }

    /**
     * Get the GPIO for the front radar trigger pin.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForFrontRadarTrig() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM24";
            default:
                throw new IllegalStateException("Unknown board " + Build.DEVICE);
        }
    }

    /**
     * Get the GPIO for the front radar echo pin.
     * <p>
     * See: https://pinout.xyz/pinout/
     */
    public static String getGPIOForFrontEcho() {
        switch (getBoardVariant()) {
            case DEVICE_RPI3:
                return "BCM23";
            default:
                throw new IllegalStateException("Unknown board " + Build.DEVICE);
        }
    }

    /**
     * Get board variant.
     *
     * @return Name of the board.
     */
    private static String getBoardVariant() {
        if (!sBoardVariant.isEmpty()) {
            return sBoardVariant;
        }
        sBoardVariant = Build.DEVICE;
        // For the edison check the pin prefix
        // to always return Edison Breakout pin name when applicable.
        if (sBoardVariant.equals(DEVICE_EDISON)) {
            PeripheralManagerService pioService = new PeripheralManagerService();
            List<String> gpioList = pioService.getGpioList();
            if (gpioList.size() != 0) {
                String pin = gpioList.get(0);
                if (pin.startsWith("IO")) {
                    sBoardVariant = DEVICE_EDISON_ARDUINO;
                }
            }
        }
        return sBoardVariant;
    }
}
