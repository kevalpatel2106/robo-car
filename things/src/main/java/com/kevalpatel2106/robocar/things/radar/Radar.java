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

import android.os.Handler;
import android.os.HandlerThread;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.kevalpatel2106.robocar.things.exception.GpoInitializationException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * This is the driver class for Ultrasonic distance measurement sensor - HC-SR04.
 * This class uses different threads to send pulses to trigger pin and get the echo pulses. This threads
 * are other than main thread. This is to increase the time accuracy for echo pulses by doing
 * simultaneous tasks.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 * @see 'https://cdn.sparkfun.com/datasheets/Sensors/Proximity/HCSR04.pdf'
 */

public abstract class Radar implements AutoCloseable {
    private static final int INTERVAL_BETWEEN_TRIGGERS = 65;    //Interval between two subsequent pulses
    private static final int TRIG_DURATION_IN_NANO = 10000;     //Trigger pulse duration

    private Gpio mEchoPin;                  //GPIO for echo
    private Gpio mTrigger;                  //GPIO for trigger
    private Handler mTriggerHandler;        //Handler for trigger.

    private boolean isTrasmitting;

    /**
     * Runnable to send trigger pulses.
     */
    private Runnable mTriggerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                sendTriggerPulse();
                mTriggerHandler.postDelayed(mTriggerRunnable, INTERVAL_BETWEEN_TRIGGERS);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Callback for {@link #mEchoPin}. This callback will be called on both edges.
     */
    private GpioCallback mEchoCallback = new GpioCallback() {
        private long mPulseStartTime;

        @Override
        public boolean onGpioEdge(Gpio gpio) {
            try {
                if (gpio.getValue()) {
                    mPulseStartTime = System.nanoTime();
                } else {
                    //Calculate distance.
                    //From data-sheet (https://cdn.sparkfun.com/datasheets/Sensors/Proximity/HCSR04.pdf)
                    //Notify callback
                    newDistance(TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - mPulseStartTime) / 58.23);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            super.onGpioError(gpio, error);
        }
    };

    /**
     * Constructor.
     */
    Radar() {
        try {
            //Set the echo pins
            mEchoPin = getEchoPin();
            mEchoPin.setDirection(Gpio.DIRECTION_IN);
            mEchoPin.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mEchoPin.setActiveType(Gpio.ACTIVE_HIGH);

            // Prepare handler for GPIO callback
            HandlerThread handlerThread = new HandlerThread("EchoCallbackHandlerThread");
            handlerThread.start();
            mEchoPin.registerGpioCallback(mEchoCallback, new Handler(handlerThread.getLooper()));

            //Set the trigger pin
            mTrigger = getTriggerPin();
            mTrigger.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }

    /**
     * Start transmitting the trigger pulses.
     */
    @SuppressWarnings("WeakerAccess")
    public void startTransmission() {
        if (isTrasmitting) return;

        isTrasmitting = true;

        //Start sending pulses
        //We are using different thread for sending pulses to increase time accuracy.
        HandlerThread triggerHandlerThread = new HandlerThread("TriggerHandlerThread");
        triggerHandlerThread.start();
        mTriggerHandler = new Handler(triggerHandlerThread.getLooper());
        mTriggerHandler.post(mTriggerRunnable);
    }

    /**
     * Stop transmitting trigger pulses.
     */
    @SuppressWarnings("WeakerAccess")
    public void stopTransmission() {
        if (isTrasmitting) {
            mTriggerHandler.removeCallbacks(mTriggerRunnable);
            isTrasmitting = false;
        }
    }

    /**
     * Abstract method to get GPIO pin for the trigger. Assign the GPIO pin, which is connected to
     * trigger pin of the sensor.
     *
     * @return {@link Gpio} for trigger.
     */
    protected abstract Gpio getTriggerPin();

    /**
     * Abstract method to get GPIO pin for the echo. Assign the GPIO pin, which is connected to
     * echo pin of the sensor.
     *
     * @return {@link Gpio} for trigger.
     */
    protected abstract Gpio getEchoPin();

    /**
     * Fire trigger pulse for {@link #TRIG_DURATION_IN_NANO} nano seconds.
     */
    private void sendTriggerPulse() throws IOException, InterruptedException {
        //Resetting trigger
        mTrigger.setValue(false);
        Thread.sleep(0, 2000);

        //Set trigger pin for 10 micro seconds.
        mTrigger.setValue(true);
        Thread.sleep(0, TRIG_DURATION_IN_NANO);

        // Reset the trigger after 10 micro seconds.
        mTrigger.setValue(false);
    }

    /**
     * Close the radar.
     *
     * @throws IOException If error occurs while closing GPIO.
     */
    @Override
    public void close() throws IOException {
        stopTransmission();
        mEchoPin.unregisterGpioCallback(mEchoCallback);
        mEchoPin.close();
        mTrigger.close();
    }

    /**
     * Abstract method to do something when the distance gets updated.
     * Whenever new distance is calculated this method will be called.
     *
     * @param distanceInCm Distance from the obstacle in cm.
     */
    protected abstract void newDistance(double distanceInCm);

    /**
     * @return Returns true if the radar is transmitting trigger pulses.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isTrasmitting() {
        return isTrasmitting;
    }
}
