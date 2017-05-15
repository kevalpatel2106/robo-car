package com.kevalpatel2106.robocar.things.motor;

import android.support.annotation.NonNull;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.exception.GpoInitializationException;
import com.kevalpatel2106.robocar.things.processor.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval Patel on 15/05/17.
 * This class mocks right side physical motors.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class RightMotor extends Motor {

    /**
     * Public constructor.
     *
     * @param service {@link PeripheralManagerService}
     */
    public RightMotor(@NonNull PeripheralManagerService service) {
        super(service);
    }


    /**
     * Get {@link Gpio} for motor driver control pin 1.
     *
     * @param service {@link PeripheralManagerService}
     * @return {@link Gpio}
     */
    @Override
    protected Gpio getControlPin1(PeripheralManagerService service) {
        try {
            return service.openGpio(BoardDefaults.getGPIOForIn1());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }

    /**
     * Get {@link Gpio} for motor driver control pin 2.
     *
     * @param service {@link PeripheralManagerService}
     * @return {@link Gpio}
     */
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
