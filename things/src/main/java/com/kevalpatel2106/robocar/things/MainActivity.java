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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.chassis.MovementController;
import com.kevalpatel2106.robocar.things.server.WebServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MovementController mMovementController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Initialize the movement controller
            mMovementController = new MovementController(this, new PeripheralManagerService());

            //Start the web server
            new WebServer(mMovementController, getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMovementController.turnOff();
    }
}
