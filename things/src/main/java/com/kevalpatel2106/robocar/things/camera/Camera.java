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
    private HandlerThread mBackgroundThread;    //Background thread to capture and process captured image.
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

        mBackgroundThread = new HandlerThread(CAMERA_THREAD_NAME);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

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
            if (mBackgroundThread != null) mBackgroundThread.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
    }

    /**
     * Start capturing the still image. Once image is captured, you can get the bitmap in
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
     * Get the bitmap image from the camera. This runs on the background thread.
     *
     * @param reader {@link ImageReader}
     * @see ImageReader.OnImageAvailableListener
     */
    @Override
    public void onImageAvailable(ImageReader reader) {
        //Get the image in bitmap.
        final Bitmap bitmap;
        try (Image image = reader.acquireNextImage()) {
            bitmap = Utils.imageToBitmap(image);
            if (bitmap != null) {
                mListener.onImageCaptured(bitmap);
                Log.d(TAG, "onImageAvailable: Byte count:" + bitmap.getByteCount());
            } else {
                mListener.onError();
                Log.e(TAG, "onImageAvailable: ImageReader did not returned any byte.");
            }
        }
    }
}