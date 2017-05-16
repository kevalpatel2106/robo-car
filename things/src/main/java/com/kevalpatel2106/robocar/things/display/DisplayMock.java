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

package com.kevalpatel2106.robocar.things.display;

import android.support.annotation.NonNull;

/**
 * Created by Keval on 16-May-17.
 * This class mocks the display of the robot.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

abstract class DisplayMock {

    DisplayMock() {
    }

    /**
     * Turn on and initialize the display.
     */
    public abstract void turnOn();

    /**
     * Write text to the display.
     *
     * @param text text to write.
     */
    public abstract void write(@NonNull String text);

    /**
     * Turn off the display.
     */
    public abstract void turnOff();
}
