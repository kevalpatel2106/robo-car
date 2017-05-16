/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.robocar.things.chassis;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;

/**
 * Created by Keval Patel on 14/05/17.
 * This class controls the movement of the robot.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class MovementController {
    private Chassis mChassis;   //Car chassis
    private boolean isLockedForObstacle = false;    //Bool to indicate if the external movement control is locked?

    private ObstacleAlertListener mFrontRadarObstacleListener = new ObstacleAlertListener() {
        @Override
        public void onObstacleDetected() {
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
    };

    /**
     * Public constructor.
     *
     * @param context instance of caller activity.
     * @param service {@link PeripheralManagerService}
     */
    public MovementController(@NonNull Context context,
                              @NonNull PeripheralManagerService service) {
        mChassis = new Chassis(context, service, mFrontRadarObstacleListener);

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
