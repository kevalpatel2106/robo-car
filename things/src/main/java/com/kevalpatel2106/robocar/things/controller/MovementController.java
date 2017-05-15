package com.kevalpatel2106.robocar.things.controller;

import android.os.Handler;
import android.support.annotation.IntDef;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.BoardDefaults;
import com.kevalpatel2106.robocar.things.ultrasonic.ProximityAlertListener;
import com.kevalpatel2106.robocar.things.ultrasonic.UltrasonicSensorDriver;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class MovementController implements ProximityAlertListener {
    private static final int MOVEMENT_FORWARD = 0;
    private static final int MOVEMENT_STOP = 1;
    private static final int MOVEMENT_REVERSE = 2;

    private final UltrasonicSensorDriver mUltrasonicSensor;

    private boolean isLocked = false;

    private Gpio mRightMotor1;
    private Gpio mRightMotor2;
    private Gpio mLeftMotor1;
    private Gpio mLeftMotor2;

    public MovementController(PeripheralManagerService service) {

        try {
            //set the right motor
            mRightMotor1 = service.openGpio(BoardDefaults.getGPIOForIn1());
            mRightMotor1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mRightMotor2 = service.openGpio(BoardDefaults.getGPIOForIn2());
            mRightMotor2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            //Set the left motor
            mLeftMotor1 = service.openGpio(BoardDefaults.getGPIOForIn3());
            mLeftMotor1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLeftMotor2 = service.openGpio(BoardDefaults.getGPIOForIn4());
            mLeftMotor2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Cannot initialize GPIO.");
        }

        //Start ultrasonic sensors
        mUltrasonicSensor = new UltrasonicSensorDriver(BoardDefaults.getGPIOForFrontTrig(),
                BoardDefaults.getGPIOForFrontEcho(),
                this,
                service);

        reset();
    }

    private void leftMotorControl(@Movement int movement) throws IOException {
        switch (movement) {
            case MOVEMENT_FORWARD:
                mLeftMotor1.setValue(true);
                mLeftMotor2.setValue(false);
                break;
            case MOVEMENT_REVERSE:
                mLeftMotor1.setValue(false);
                mLeftMotor2.setValue(true);
                break;
            case MOVEMENT_STOP:
                mLeftMotor1.setValue(false);
                mLeftMotor2.setValue(false);
                break;
        }
    }

    private void rightMotorControl(@Movement int movement) throws IOException {
        switch (movement) {
            case MOVEMENT_FORWARD:
                mRightMotor1.setValue(true);
                mRightMotor2.setValue(false);
                break;
            case MOVEMENT_REVERSE:
                mRightMotor1.setValue(false);
                mRightMotor2.setValue(true);
                break;
            case MOVEMENT_STOP:
                mRightMotor1.setValue(false);
                mRightMotor2.setValue(false);
                break;
        }
    }

    public void turnLeft() {
        if (isLocked) return;
        try {
            leftMotorControl(MOVEMENT_REVERSE);
            rightMotorControl(MOVEMENT_FORWARD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void turnRight() {
        if (isLocked) return;
        try {
            leftMotorControl(MOVEMENT_FORWARD);
            rightMotorControl(MOVEMENT_REVERSE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveForward() {
        if (isLocked) return;
        try {
            leftMotorControl(MOVEMENT_FORWARD);
            rightMotorControl(MOVEMENT_FORWARD);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void moveReverse() {
        if (isLocked) return;
        forceReverse();

    }

    private void forceReverse() {
        try {
            leftMotorControl(MOVEMENT_REVERSE);
            rightMotorControl(MOVEMENT_REVERSE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            leftMotorControl(MOVEMENT_STOP);
            rightMotorControl(MOVEMENT_STOP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProximityAlert() {
        isLocked = true;

        forceReverse();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stop();
                isLocked = false;
            }
        }, 400);
    }

    public void close() throws Exception {
        mUltrasonicSensor.close();
        mLeftMotor1.close();
        mLeftMotor2.close();
        mRightMotor1.close();
        mRightMotor2.close();
    }

    public void reset() {
        stop();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MOVEMENT_FORWARD, MOVEMENT_REVERSE, MOVEMENT_STOP})
    @interface Movement {
    }
}
