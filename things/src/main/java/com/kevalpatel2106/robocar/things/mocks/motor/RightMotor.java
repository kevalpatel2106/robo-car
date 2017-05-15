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

public final class RightMotor extends RoboMotor {

    private Gpio mRightMotor1;
    private Gpio mRightMotor2;

    public RightMotor(PeripheralManagerService service) {
        super(service);
    }

    @Override
    protected void setGpio(PeripheralManagerService service) {
        //Set the left motor
        try {
            mRightMotor1 = service.openGpio(BoardDefaults.getGPIOForIn1());
            mRightMotor1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mRightMotor2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mRightMotor2 = service.openGpio(BoardDefaults.getGPIOForIn2());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Cannot initialize GPIO.");
        }
    }

    @Override
    public void startForward() {
        try {
            mRightMotor1.setValue(true);
            mRightMotor2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startReverse() {
        try {
            mRightMotor1.setValue(false);
            mRightMotor2.setValue(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            mRightMotor1.setValue(false);
            mRightMotor2.setValue(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws Exception {
        mRightMotor1.close();
        mRightMotor2.close();
    }
}
