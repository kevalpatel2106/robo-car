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

package com.kevalpatel2106.robocar.things.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kevalpatel2106.robocar.things.Utils;

/**
 * Created by Keval Patel on 17/05/17.
 * Representation of the robot camera. It provides methods to handle camera operations.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class Camera extends CameraMock implements ImageReader.OnImageAvailableListener {
    private static final String TAG = Camera.class.getSimpleName();
    private static final String CAMERA_THREAD_NAME = "CameraThread";
    @NonNull
    private final Context mContext;
    private CameraCaptureListener mListener;

    private Handler mBackgroundHandler;
    private HandlerThread mCameraThread;    //Background thread to capture and process captured image.
    private PiCameraDriver mPiCamera;           //Pi camera.

    /**
     * Public constructor.
     *
     * @param context instance of thr caller.
     */
    public Camera(@NonNull Context context,
                  @NonNull CameraCaptureListener listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * Turn on the camera.
     */
    @Override
    public void turnOn() {
        if (mBackgroundHandler != null) turnOff();

        mCameraThread = new HandlerThread(CAMERA_THREAD_NAME);
        mCameraThread.start();
        mBackgroundHandler = new Handler(mCameraThread.getLooper());

        //Initialize the camera.
        mPiCamera = PiCameraDriver.getInstance();
        mPiCamera.initializeCamera(mContext, mBackgroundHandler, Camera.this);
    }

    /**
     * Close the camera resources.
     */
    @Override
    public void turnOff() {
        try {
            if (mPiCamera != null) mPiCamera.shutDown();
            if (mCameraThread != null) mCameraThread.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            mCameraThread = null;
            mBackgroundHandler = null;
        }
    }

    /**
     * Start capturing the still image. Once image is captured, you can get the sOutBitmap in
     * {@link CameraCaptureListener}.
     *
     * @see CameraCaptureListener#onImageCaptured(Bitmap)
     */
    @Override
    public void takePicture() {
        if (!isCameraInitialized()) throw new RuntimeException("Camera not initialized.");
        //Take picture using pi camera.
        if (mPiCamera != null) mPiCamera.takePicture();
    }

    /**
     * @return True if the camera is initialized.
     */
    @Override
    public boolean isCameraInitialized() {
        return mPiCamera != null;
    }

    /**
     * Get the sOutBitmap image from the camera. This runs on the background thread.
     * This function works on the {@link Camera#mCameraThread}.
     *
     * @param reader {@link ImageReader}
     * @see ImageReader.OnImageAvailableListener
     */
    @Override
    public void onImageAvailable(ImageReader reader) {
        //Get the image in sOutBitmap.
        try (Image image = reader.acquireNextImage()) {
            Bitmap sOutBitmap = Utils.imageToBitmap(image);
            image.close();

            if (sOutBitmap != null) {
                mListener.onImageCaptured(sOutBitmap);
            } else {
                mListener.onError();
                Log.e(TAG, "onImageAvailable: ImageReader did not returned any byte.");
            }
        }
    }
}