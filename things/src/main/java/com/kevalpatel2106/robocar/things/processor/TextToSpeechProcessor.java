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
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Keval Patel on 25/04/17.
 * This class initialize and maintain the connection with TextToSpeechProcessor engine.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class TextToSpeechProcessor {
    private static final String TAG = TextToSpeechProcessor.class.getSimpleName();
    private static final String UTTERANCE_ID = "com.kevalpatel2106.robocar.things.UTTERANCE_ID";

    private static TextToSpeech mTTSEngine;

    /**
     * Stop current utterance and flush the TextToSpeechProcessor queue.
     */
    public static void flush() {
        if (mTTSEngine != null) mTTSEngine.stop();
    }

    /**
     * Pass the text and let TextToSpeechProcessor speak.
     *
     * @param text text to speak
     */
    public static void speak(Context context, final String text) {
        Log.d(TAG, "Speak : " + text);
        if (mTTSEngine == null) {
            mTTSEngine = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i == TextToSpeech.SUCCESS) {
                        mTTSEngine.setLanguage(Locale.US);
                        mTTSEngine.setPitch(1f);
                        mTTSEngine.setSpeechRate(1f);

                        mTTSEngine.speak(text, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
                    } else {
                        mTTSEngine = null;
                    }
                }
            });
        } else {
            mTTSEngine.speak(text, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
        }
    }

    public static boolean isIintilized() {
        return mTTSEngine != null;
    }
}