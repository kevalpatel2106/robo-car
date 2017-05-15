package com.kevalpatel2106.robocar.things.motor;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.exception.GpoInitializationException;
import com.kevalpatel2106.robocar.things.processor.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval Patel on 15/05/17.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class RightMotor extends Motor {

    public RightMotor(PeripheralManagerService service) {
        super(service);
    }

    @Override
    protected Gpio getControlPin1(PeripheralManagerService service) {
        try {
            return service.openGpio(BoardDefaults.getGPIOForIn1());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }

    @Override
    protected Gpio getControlPin2(PeripheralManagerService service) {
        try {
            return service.openGpio(BoardDefaults.getGPIOForIn2());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }
}
