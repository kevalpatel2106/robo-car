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
import android.util.Log;

import com.kevalpatel2106.robocar.things.processor.BoardDefaults;

import java.io.IOException;

/**
 * Created by Keval on 16-May-17.
 * Front display.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class LcdDisplay extends DisplayMock {
    private static final String TAG = LcdDisplay.class.getSimpleName();
    private LCD1602Driver mLcd;

    /**
     * Turn on and initialize the display.
     */
    @Override
    public void turnOn() {
        try {
            mLcd = new LCD1602Driver(BoardDefaults.getGPIOForLCDRs(),
                    BoardDefaults.getGPIOForLCDEn(),
                    BoardDefaults.getGPIOForLCDD4(),
                    BoardDefaults.getGPIOForLCDD5(),
                    BoardDefaults.getGPIOForLCDD6(),
                    BoardDefaults.getGPIOForLCDD7());
            mLcd.begin(16, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write text to the display.
     *
     * @param text text to write. Make sure that max line length is 16 and you provide maximum 2 lines.
     */
    @Override
    public void write(@NonNull String text) {
        try {

            Log.d(TAG, "write: Printing - " + text);
            mLcd.clear();

            //Split by new lines.
            String[] lines = text.split("\n");
            if (lines.length > 2) throw new RuntimeException("Cannot print more than 2 lines.");

            //Print the line 1
            for (String line : lines) {
                mLcd.print(line);
                //Move the cursor to line 2
                mLcd.setCursor(0, 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turn off the display.
     */
    @Override
    public void turnOff() {
        if (mLcd != null) {
            try {
                mLcd.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mLcd = null;
            }
        }
    }
}
