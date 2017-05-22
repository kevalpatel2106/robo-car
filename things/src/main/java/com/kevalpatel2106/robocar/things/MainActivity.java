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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.kevalpatel2106.robocar.things.processor.SpeechProcessorService;
import com.kevalpatel2106.robocar.things.server.WebServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Controller mController;

    private boolean isSpeechProcessorBound;
    /**
     * Service connection listener for {@link SpeechProcessorService}.
     */
    private ServiceConnection mSpeechRecognitionServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SpeechProcessorService.LocalBinder binder = (SpeechProcessorService.LocalBinder) service;
            isSpeechProcessorBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isSpeechProcessorBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            //Initialize the movement controller
            mController = new Controller(this);
            mController.setSocketWriter(new WebServer(mController, getAssets()));

            //Bind the speech processor
            Intent intent = new Intent(this, SpeechProcessorService.class);
            bindService(intent, mSpeechRecognitionServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mController.turnOff();

        // Unbind the speech processor
        if (isSpeechProcessorBound) {
            unbindService(mSpeechRecognitionServiceConnection);
            isSpeechProcessorBound = false;
        }
    }
}
