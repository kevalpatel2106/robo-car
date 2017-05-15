package com.kevalpatel2106.robocar.things.motor;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.exception.GpoInitializationException;

import java.io.IOException;

/**
 * Created by Keval Patel on 15/05/17.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

abstract class Motor implements AutoCloseable {

    private Gpio mMotorControlPin1;
    private Gpio mMotorControlPin2;

    Motor(PeripheralManagerService service) {
        try {
            mMotorControlPin1 = getControlPin1(service);
            mMotorControlPin1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            mMotorControlPin2 = getControlPin2(service);
            mMotorControlPin2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }

    protected abstract Gpio getControlPin1(PeripheralManagerService service);

    protected abstract Gpio getControlPin2(PeripheralManagerService service);

    public void forward() {
        try {
            mMotorControlPin1.setValue(true);
            mMotorControlPin2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reverse() {
        try {
            mMotorControlPin1.setValue(false);
            mMotorControlPin2.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            mMotorControlPin1.setValue(false);
            mMotorControlPin2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        mMotorControlPin1.close();
        mMotorControlPin2.close();
    }
}