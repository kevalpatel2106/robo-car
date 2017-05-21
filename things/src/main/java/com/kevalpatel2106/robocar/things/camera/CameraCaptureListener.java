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
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

/**
 * Created by Keval on 17-May-17.
 * Listener to get notify when image is captured.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public interface CameraCaptureListener {

    /**
     * Callback to receive when image capture in completed. This function works on the {@link Camera#mCameraThread}.
     *
     * @param bitmap Captured {@link Bitmap}
     */
    @WorkerThread
    void onImageCaptured(@NonNull Bitmap bitmap);

    /**
     * Callback to receive when error occurred in image captured. This function works on the {@link Camera#mCameraThread}.
     */
    @WorkerThread
    void onError();
}
