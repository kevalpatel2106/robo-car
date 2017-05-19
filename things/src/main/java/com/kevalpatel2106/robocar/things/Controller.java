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

package com.kevalpatel2106.robocar.things;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.camera.CameraCaptureListener;
import com.kevalpatel2106.robocar.things.chassis.Chassis;
import com.kevalpatel2106.robocar.things.radar.ObstacleAlertListener;
import com.kevalpatel2106.robocar.things.server.CommandSender;
import com.kevalpatel2106.robocar.things.server.WebServer;
import com.kevalpatel2106.tensorflow.Classifier;
import com.kevalpatel2106.tensorflow.TensorFlowImageClassifier;

import java.io.IOException;
import java.util.List;

/**
 * Created by Keval Patel on 14/05/17.
 * The class that controls the robot.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class Controller implements CameraCaptureListener {
    private static final String TAG = Controller.class.getSimpleName();
    @NonNull
    private final Context mContext;
    private final Chassis mChassis;                       //Car chassis

    private CommandSender mCommandSender;

    private boolean isLockedForObstacle = false;    //Bool to indicate if the external movement control is locked?

    /**
     * {@link ObstacleAlertListener} to prevent collision with the object using front radar.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private ObstacleAlertListener mFrontRadarObstacleListener = new ObstacleAlertListener() {
        @Override
        public void onObstacleDetected() {
            if (mChassis == null) return;

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
     */
    public Controller(@NonNull Context context) {
        mContext = context;
        PeripheralManagerService service = new PeripheralManagerService();
        mChassis = new Chassis.Builder()
                .mountRightMotor(service)
                .mountLeftMotor(service)
                .mountFrontRadar(mFrontRadarObstacleListener)
                .mountBeacon(context)
                .mountCamera(context, this)
                .build();

        //Reset the motion
        stop();
    }

    public void setCommandSender(WebServer server) {
        mCommandSender = server.getListner();
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

    public void captureImage() {
        if (mChassis.getCamera() != null) mChassis.getCamera().takePicture();
    }

    /**
     * Turn off the movement and release the resources.
     */
    @SuppressWarnings("WeakerAccess")
    public void turnOff() {
        try {
            mChassis.turnOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageCaptured(@NonNull Bitmap bitmap) {
        Log.d(TAG, "onImageCaptured: " + bitmap.getByteCount());
        try {
            mCommandSender.sendImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        processImage(bitmap);
    }

    private void processImage(@NonNull Bitmap bitmap) {
        TensorFlowImageClassifier inferenceInterface = new TensorFlowImageClassifier(mContext);
        List<Classifier.Recognition> results = inferenceInterface.recognizeImage(bitmap);

        for (Classifier.Recognition result : results)
            Log.d(TAG, "onImageCaptured: Result =>" + result.getTitle() + " " + result.getConfidence());
    }

    @Override
    public void onError() {
        //TODO handle error.
    }
}
