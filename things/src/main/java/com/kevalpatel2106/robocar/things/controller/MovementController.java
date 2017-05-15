package com.kevalpatel2106.robocar.things.controller;

import android.os.Handler;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.BoardDefaults;
import com.kevalpatel2106.robocar.things.mocks.motor.LeftMotor;
import com.kevalpatel2106.robocar.things.mocks.motor.RightMotor;
import com.kevalpatel2106.robocar.things.mocks.ultrasonic.Hcsr04;
import com.kevalpatel2106.robocar.things.mocks.ultrasonic.ProximityAlertListener;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class MovementController implements ProximityAlertListener {
    private final Hcsr04 mUltrasonicSensor;
    private RightMotor mRightMotor;
    private LeftMotor mLeftMotor;

    private boolean isLocked = false;

    public MovementController(PeripheralManagerService service) {
        mLeftMotor = new LeftMotor(service);
        mRightMotor = new RightMotor(service);

        //Start ultrasonic sensors
        mUltrasonicSensor = new Hcsr04(BoardDefaults.getGPIOForFrontTrig(),
                BoardDefaults.getGPIOForFrontEcho(),
                this,
                service);

        reset();
    }

    public void turnLeft() {
        if (isLocked) return;

        mLeftMotor.startReverse();
        mRightMotor.startForward();
    }

    public void turnRight() {
        if (isLocked) return;

        mLeftMotor.startForward();
        mRightMotor.startReverse();
    }

    public void moveForward() {
        if (isLocked) return;

        mLeftMotor.startForward();
        mRightMotor.startForward();
    }

    public void moveReverse() {
        if (isLocked) return;
        forceReverse();
    }

    private void forceReverse() {
        mLeftMotor.startReverse();
        mRightMotor.startReverse();
    }

    public void stop() {
        mLeftMotor.stop();
        mRightMotor.stop();
    }

    @Override
    public void onProximityDistanceChange(double distanceInCm) {
        if (distanceInCm > 40) return;
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
        mLeftMotor.close();
        mRightMotor.close();
    }

    public void reset() {
        stop();
    }
}
