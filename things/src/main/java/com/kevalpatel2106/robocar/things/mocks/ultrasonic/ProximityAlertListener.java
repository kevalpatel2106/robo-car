package com.kevalpatel2106.robocar.things.mocks.ultrasonic;

/**
 * Callback listener to get notified when proximity alert triggers.
 */

public interface ProximityAlertListener {
    void onProximityDistanceChange(double distance);
}
