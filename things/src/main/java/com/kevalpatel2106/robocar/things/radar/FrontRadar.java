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

package com.kevalpatel2106.robocar.things.radar;

import android.support.annotation.NonNull;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.exception.GpioInitializationException;
import com.kevalpatel2106.robocar.things.processor.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval on 15-May-17.
 * Class to mock radar at the front of the car.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public class FrontRadar extends RadarMock implements Hcsr04.DistanceListener {
    private static final double DISTANCE_THRASHOLD_IN_CM = 40;
    private ObstacleAlertListener mListener;

    private Hcsr04 mHcsr04;

    /**
     * Public constructor.
     */
    public FrontRadar(@NonNull ObstacleAlertListener listener) {
        super();

        //Initialize GPIO
        try {
            PeripheralManagerService service = new PeripheralManagerService();
            Gpio trigPin = service.openGpio(BoardDefaults.getGPIOForFrontRadarTrig());
            Gpio echoPin = service.openGpio(BoardDefaults.getGPIOForFrontEcho());

            mHcsr04 = new Hcsr04(echoPin, trigPin, this);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpioInitializationException();
        }

        mListener = listener;
    }

    @Override
    public void startTransmission() {
        mHcsr04.startTransmission();
    }

    @Override
    public void stopTransmission() {
        mHcsr04.stopTransmission();
    }

    @Override
    public void turnOff() {
        mHcsr04.close();
    }

    /**
     * Whenever new distance is calculated this method will be called.
     *
     * @param newDistance Distance from the obstacle in cm.
     */
    @Override
    public void onDistanceUpdated(double newDistance) {
        if (newDistance < DISTANCE_THRASHOLD_IN_CM) {
            mListener.onObstacleDetected();
        }
    }
}
