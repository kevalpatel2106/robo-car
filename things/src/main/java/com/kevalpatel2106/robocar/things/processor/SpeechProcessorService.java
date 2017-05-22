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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.kevalpatel2106.pocketsphinx.PocketSphinx;
import com.kevalpatel2106.pocketsphinx.PocketSphinxListener;

/**
 * Created by Keval Patel on 05/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class SpeechProcessorService extends Service implements PocketSphinxListener {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private PocketSphinx pocketSphinx;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pocketSphinx = new PocketSphinx(this, this);
    }

    @Override
    public void onSpeechRecognizerReady() {
        TextToSpeechProcessor.speak(this, "I'm listening!");
        pocketSphinx.startListeningToActivationPhrase();
    }

    @Override
    public void onActivationPhraseDetected() {
        TextToSpeechProcessor.speak(this, "Yes");
        pocketSphinx.startListeningToAction();
    }

    @Override
    public void onTextRecognized(String recognizedText) {
        pocketSphinx.startListeningToActivationPhrase();

        //Todo handle recognized text
    }

    @Override
    public void onTimeout() {
        pocketSphinx.startListeningToActivationPhrase();
    }

    /**
     * A binder object to return to the activity which binds this service.
     */
    public class LocalBinder extends Binder {
        @SuppressWarnings("unused")
        public SpeechProcessorService getService() {
            // Return this instance of SpeechProcessorService so clients can call public methods
            return SpeechProcessorService.this;
        }
    }
}
