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
    public static final String STOP = "stop";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TURN_LEFT, TURN_RIGHT, MOVE_FORWARD, MOVE_REVERSE, STOP})
    public @interface Command {
    }
}
