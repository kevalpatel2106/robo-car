package com.kevalpatel2106.robocar.things.chassis;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;
import com.kevalpatel2106.robocar.things.radar.Radar;

/**
 * Created by Keval Patel on 14/05/17.
 * This class controls the movement of the robot.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class MovementController implements ObstacleAlertListener {
    private Chassis mChassis;   //Car chassis
    private boolean isLockedForObstacle = false;    //Bool to indicate if the external movement control is locked?

    /**
     * Public constructor.
     *
     * @param context instance of caller activity.
     * @param service {@link PeripheralManagerService}
     */
    public MovementController(@NonNull Context context,
                              @NonNull PeripheralManagerService service) {
        mChassis = new Chassis(context, service, this);

        //Reset the motion
        stop();
    }

    /**
     * Turn to robot to the left.
     */
    public void turnLeft() {
        if (isLockedForObstacle) return;
        mChassis.turnLeftInternal();
    }

    /**
     * Turn the robot to right.
     */
    public void turnRight() {
        if (isLockedForObstacle) return;
        mChassis.turnRightInternal();
    }

    /**
     * Move robot to forward direction.
     */
    public void moveForward() {
        if (isLockedForObstacle) return;
        mChassis.moveForwardInternal();
    }

    /**
     * Move the robot to reverse direction.
     */
    public void moveReverse() {
        if (isLockedForObstacle) return;
        mChassis.moveReverseInternal();
    }

    /**
     * Stop the robot. (Free spin)
     */
    public void stop() {
        mChassis.stopInternal();
    }

    @Override
    public void onProximityAlert(Radar radar) {
        if (radar == mChassis.getFrontRadar()) {
            isLockedForObstacle = true;     //Lock external movement

            mChassis.moveReverseInternal();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                    isLockedForObstacle = false;     //Release external movement control
                }
            }, 400);
        }
    }

    /**
     * Turn off the movement and release the resources.
     */
    public void turnOff() {
        try {
            mChassis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
