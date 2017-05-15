package com.kevalpatel2106.robocar.things.radar;

/**
 * Callback listener to get notified when proximity alert triggers for any radar.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public interface ObstacleAlertListener {

    /**
     * Method to execute when obstacle is detected.
     *
     * @param radar instance of {@link Radar} which detected obstacle.
     */
    void onProximityAlert(Radar radar);
}
