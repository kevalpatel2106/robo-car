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

import android.graphics.Bitmap;

/**
 * Created by Keval Patel on 17/05/17.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

abstract class CameraMock {

    public abstract void turnOn();

    /**
     * Close the camera resources.
     */
    public abstract void turnOff();

    /**
     * Start capturing the still image. Once image is captured, you can get the bitmap in
     * {@link CameraCaptureListener}.
     *
     * @see CameraCaptureListener#onImageCaptured(Bitmap)
     */
    public abstract void takePicture();

    /**
     * Check if the cam,era is initialized.
     *
     * @return true if the camera in initialized.
     */
    public abstract boolean isCameraInitialized();
}
