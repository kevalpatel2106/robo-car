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

package com.kevalpatel2106.robocar.things.processor;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Keval Patel on 23/05/17.
 * This class handles all the thread for different operations.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class ThreadManager {

    private static HandlerThread sRadarThread;
    private static HandlerThread sCameraThread;

    public static Handler getTensorflowHandler() {
        HandlerThread tfHandler = new HandlerThread("TfHandler");
        tfHandler.start();
        return new Handler(tfHandler.getLooper());
    }


    public static HandlerThread getRadarHandlerThread() {
        if (sRadarThread != null && sRadarThread.isAlive())
            return sRadarThread;

        sRadarThread = new HandlerThread("RadarHandler");
        sRadarThread.start();
        return sRadarThread;
    }

    public static HandlerThread getCameraThread() {
        if (sCameraThread != null && sCameraThread.isAlive())
            return sCameraThread;

        sCameraThread = new HandlerThread("CameraThread");
        sCameraThread.start();
        return sCameraThread;
    }


}
