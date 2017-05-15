package com.kevalpatel2106.robocar.things.mocks.motor;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval Patel on 15/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class LeftMotor extends RoboMotor {

    private Gpio mLeftMotor1;
    private Gpio mLeftMotor2;

    public LeftMotor(PeripheralManagerService service) {
        super(service);
    }

    @Override
    protected void setGpio(PeripheralManagerService service) {
        //Set the left motor
        try {
            mLeftMotor1 = service.openGpio(BoardDefaults.getGPIOForIn3());
            mLeftMotor1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLeftMotor2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLeftMotor2 = service.openGpio(BoardDefaults.getGPIOForIn4());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Cannot initialize GPIO.");
        }
    }

    @Override
    public void startForward() {
        try {
            mLeftMotor1.setValue(true);
            mLeftMotor2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startReverse() {
        try {
            mLeftMotor1.setValue(false);
            mLeftMotor2.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            mLeftMotor1.setValue(false);
            mLeftMotor2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        mLeftMotor1.close();
        mLeftMotor2.close();
    }
}
