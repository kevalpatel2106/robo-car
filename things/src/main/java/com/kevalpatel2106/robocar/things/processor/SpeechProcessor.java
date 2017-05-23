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

import android.content.Context;

import com.kevalpatel2106.pocketsphinx.PocketSphinx;
import com.kevalpatel2106.pocketsphinx.PocketSphinxListener;
import com.kevalpatel2106.robocar.things.Controller;

/**
 * Created by Keval Patel on 05/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class SpeechProcessor implements PocketSphinxListener {
    private final Context mContext;
    private final Controller mController;
    private PocketSphinx mPocketSphinx;

    public SpeechProcessor(Context context, Controller controller) {
        mContext = context;
        mController = controller;

        mPocketSphinx = new PocketSphinx(context, this);
    }

    @Override
    public void onSpeechRecognizerReady() {
        TextToSpeechProcessor.speak(mContext, "I'm listening!");
        mPocketSphinx.startListeningToActivationPhrase();
    }

    @Override
    public void onActivationPhraseDetected() {
        TextToSpeechProcessor.speak(mContext, "Yes");
        mPocketSphinx.startListeningToAction();
    }

    @Override
    public void onTextRecognized(String recognizedText) {
        mPocketSphinx.startListeningToActivationPhrase();

        //Todo handle recognized text
    }

    @Override
    public void onTimeout() {
        mPocketSphinx.startListeningToActivationPhrase();
    }
}
