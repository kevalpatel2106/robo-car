package com.kevalpatel2106.robocar.things.radar;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.exception.GpoInitializationException;
import com.kevalpatel2106.robocar.things.processor.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval on 15-May-17.
 * Class to mock radar at the front of the car.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public class FrontRadar extends Radar {
    private static final double DISTANCE_THRASHOLD_IN_CM = 40;
    private ObstacleAlertListener mListener;

    /**
     * Public constructor.
     */
    public FrontRadar(ObstacleAlertListener listener) {
        super();
        mListener = listener;
    }

    /**
     * Assign the GPIO pin, which is connected to trigger pin of the sensor.
     *
     * @return {@link Gpio} for trigger.
     */
    @Override
    protected Gpio getTriggerPin() {
        try {
            return new PeripheralManagerService().openGpio(BoardDefaults.getGPIOForFrontRadarTrig());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }

    /**
     * Assign the GPIO pin, which is connected to echo pin of the sensor.
     *
     * @return {@link Gpio} for echo.
     */
    @Override
    protected Gpio getEchoPin() {
        try {
            return new PeripheralManagerService().openGpio(BoardDefaults.getGPIOForFrontEcho());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GpoInitializationException();
        }
    }

    /**
     * Whenever new distance is calculated this method will be called.
     *
     * @param distanceInCm Distance from the obstacle in cm.
     */
    @Override
    protected void newDistance(double distanceInCm) {
        if (distanceInCm < DISTANCE_THRASHOLD_IN_CM) mListener.onProximityAlert(this);
    }
}
