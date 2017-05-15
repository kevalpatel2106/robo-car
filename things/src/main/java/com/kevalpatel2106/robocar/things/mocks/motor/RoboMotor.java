package com.kevalpatel2106.robocar.things.mocks.motor;

import com.google.android.things.pio.PeripheralManagerService;

/**
 * Created by Keval Patel on 15/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

abstract class RoboMotor implements AutoCloseable {

    public RoboMotor(PeripheralManagerService service) {
        setGpio(service);
    }

    protected abstract void setGpio(PeripheralManagerService service);

    public abstract void startForward();

    public abstract void startReverse();

    public abstract void stop();
}
