package com.kevalpatel2106.robocar.things.chassis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.beacon.Beacon;
import com.kevalpatel2106.robocar.things.motor.LeftMotor;
import com.kevalpatel2106.robocar.things.motor.RightMotor;
import com.kevalpatel2106.robocar.things.radar.FrontRadar;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;

/**
 * Created by Keval on 15-May-17.
 * This class represents the chassis of the robot car.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

final class Chassis extends ChassisMock {
    @NonNull
    private final Context mContext;
    @NonNull
    private final PeripheralManagerService mService;
    @NonNull
    private final ObstacleAlertListener mListener;

    /**
     * Public constructor.
     *
     * @param context  Instance of caller
     * @param service  {@link PeripheralManagerService}
     * @param listener {@link ObstacleAlertListener}
     */
    Chassis(@NonNull Context context,
            @NonNull PeripheralManagerService service,
            @NonNull ObstacleAlertListener listener) {
        super();
        mContext = context;
        mService = service;
        mListener = listener;
    }

    @Override
    protected void init() {
        if (getFrontRadar() != null) getFrontRadar().startTransmission();
        if (getBeacon() != null) getBeacon().initBeaconTransmission();
    }

    /**
     * Mount the front radar. If your car does not have front radar pass null.
     *
     * @return {@link FrontRadar}
     */
    @Override
    @Nullable
    protected FrontRadar mountFrontRadar() {
        return new FrontRadar(mListener);
    }

    /**
     * Mount the left side motor. It's value cannot be null.
     *
     * @return {@link LeftMotor}
     */
    @Override
    @NonNull
    protected LeftMotor mountLeftMotor() {
        return new LeftMotor(mService);
    }

    /**
     * Mount the right side motor. It's value cannot be null.
     *
     * @return {@link RightMotor}
     */
    @Override
    @NonNull
    protected RightMotor mountRightMotor() {
        return new RightMotor(mService);
    }

    /**
     * Mount the beacon. If your car does not have beacon pass null.
     *
     * @return {@link Beacon}
     */
    @Override
    @Nullable
    protected Beacon mountBeacon() {
        return new Beacon(mContext);
    }

    /**
     * Take a left turn.
     */
    void turnLeftInternal() {
        getLeftMotor().reverse();
        getRightMotor().forward();
    }

    /**
     * Take a right turn.
     */
    void turnRightInternal() {
        getLeftMotor().forward();
        getRightMotor().reverse();
    }

    /**
     * Move forward.
     */
    void moveForwardInternal() {
        getLeftMotor().forward();
        getRightMotor().forward();
    }

    /**
     * Move reverse.
     */
    void moveReverseInternal() {
        getLeftMotor().reverse();
        getRightMotor().reverse();
    }

    /**
     * Stop the motor.
     */
    void stopInternal() {
        getLeftMotor().stop();
        getRightMotor().stop();
    }

    @Override
    public void close() throws Exception {
        getLeftMotor().close();
        getRightMotor().close();

        if (getFrontRadar() != null) getFrontRadar().close();
        if (getBeacon() != null) getBeacon().stopBeaconTransmission();
    }
}
