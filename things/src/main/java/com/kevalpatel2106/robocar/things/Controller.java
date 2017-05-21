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

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Keval Patel on 14/05/17.
 * The class that controls the robot.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class Controller implements CameraCaptureListener {
    private static final String TAG = Controller.class.getSimpleName();
    private final Chassis mChassis;                       //Car chassis
    private final TensorFlowImageClassifier mTfInterface;

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
    private Consumer<List<Classifier.Recognition>> mTfProcessorObserver = new Consumer<List<Classifier.Recognition>>() {
        @Override
        public void accept(@io.reactivex.annotations.NonNull List<Classifier.Recognition> recognitions) throws Exception {
            String resultStr = "";
            for (Classifier.Recognition result : recognitions) {
                resultStr = resultStr + result.getTitle() + " " + result.getConfidence() + "<br/>";
                mCommandSender.sendMessage(resultStr);
            }
        }
    };

    /**
     * Public constructor.
     *
     * @param context instance of caller activity.
     */
    public Controller(@NonNull Context context) {
        mTfInterface = new TensorFlowImageClassifier(context);

        //Build chassis.
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
        mCommandSender = server.getListener();
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
        Log.d(TAG, "captureImage: Captured " + System.currentTimeMillis());
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
        mCommandSender.sendImage(bitmap);
        processImage(bitmap);
        captureImage();
    }

    private void processImage(@NonNull final Bitmap bitmap) {
        Flowable<List<Classifier.Recognition>> observable = Flowable
                .create(new FlowableOnSubscribe<List<Classifier.Recognition>>() {
                    @Override
                    public void subscribe(FlowableEmitter<List<Classifier.Recognition>> emitter) throws Exception {
                        emitter.onNext(mTfInterface.recognizeImage(bitmap));
                    }
                }, BackpressureStrategy.MISSING);

        observable.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(mTfProcessorObserver);
    }

    @Override
    public void onError() {
        //TODO handle error.
    }
}
