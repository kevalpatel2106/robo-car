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

package com.kevalpatel2106.common;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class RoboCommands {
    public static final String TURN_LEFT = "left";
    public static final String TURN_RIGHT = "right";
    public static final String MOVE_FORWARD = "forward";
    public static final String MOVE_REVERSE = "reverse";
    public static final String TAKE_PIC = "capture";
    public static final String STOP = "stop";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TURN_LEFT, TURN_RIGHT, MOVE_FORWARD, MOVE_REVERSE, STOP})
    public @interface Command {
    }
}
