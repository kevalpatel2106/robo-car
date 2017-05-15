package com.kevalpatel2106.robocar.things.chassis;

import android.content.Context;
import android.os.Handler;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;
import com.kevalpatel2106.robocar.things.radar.Radar;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class MovementController implements ObstacleAlertListener {
    private Chassis mChassis;
    private boolean isLockedForObstacle = false;

    public MovementController(Context context, PeripheralManagerService service) {
        mChassis = new Chassis(context, service, this);

        //Reset the motion
        stop();
    }

    public void turnLeft() {
        if (isLockedForObstacle) return;
        mChassis.turnLeftInternal();
    }

    public void turnRight() {
        if (isLockedForObstacle) return;
        mChassis.turnRightInternal();
    }

    public void moveForward() {
        if (isLockedForObstacle) return;
        mChassis.moveForwardInternal();
    }

    public void moveReverse() {
        if (isLockedForObstacle) return;
        mChassis.moveReverseInternal();
    }

    public void stop() {
        mChassis.stopInternal();
    }

    @Override
    public void onProximityAlert(Radar radar) {
        if (radar == mChassis.getFrontRadar()) {
            isLockedForObstacle = true;

            mChassis.moveReverseInternal();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                    isLockedForObstacle = false;
                }
            }, 400);
        }
    }

    public void turnOff() {
        try {
            mChassis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
