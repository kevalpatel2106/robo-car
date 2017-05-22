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

package com.kevalpatel2106.pocketsphinx;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class PocketSphinx implements RecognitionListener {

    private static final String TAG = PocketSphinx.class.getSimpleName();
    private static final String ACTIVATION_KEYPHRASE = "ok things";

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String WAKEUP_SEARCH = "wakeup";
    private static final String ACTION_SEARCH = "action";

    private final PocketSphinxListener listener;
    private SpeechRecognizer recognizer;

    public PocketSphinx(Context context, PocketSphinxListener listener) {
        this.listener = listener;
        runRecognizerSetup(context);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");

        if (!recognizer.getSearchName().equals(WAKEUP_SEARCH)) {
            Log.i(TAG, "End of speech. Stop recognizer");
            recognizer.stop();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();
        if (text.equals(ACTIVATION_KEYPHRASE)) {
            Log.i(TAG, "Activation keyphrase detected during a partial result");
            recognizer.stop();
        } else {
            Log.i(TAG, "On partial result: " + text);
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();
        Log.i(TAG, "On result: " + text);

        if (ACTIVATION_KEYPHRASE.equals(text)) {
            listener.onActivationPhraseDetected();
        } else {
            listener.onTextRecognized(text);
        }
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "On error", e);
    }

    @Override
    public void onTimeout() {
        Log.i(TAG, "Timeout!");
        recognizer.stop();
        listener.onTimeout();
    }

    public void startListeningToActivationPhrase() {
        Log.i(TAG, "Start listening for the \"ok things\" keyphrase");
        recognizer.startListening(WAKEUP_SEARCH);
    }

    public void startListeningToAction() {
        Log.i(TAG, "Start listening for some actions with a 10secs timeout");
        recognizer.startListening(ACTION_SEARCH, 10000);
    }

    public void onDestroy() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    private void runRecognizerSetup(final Context context) {
        Log.d(TAG, "Recognizer setup");

        // Recognizer initialization is a time-consuming and it involves IO, so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.e(TAG, "Failed to initialize recognizer: " + result);
                } else {
                    listener.onSpeechRecognizerReady();
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();
        recognizer.addListener(this);

        // Custom recognizer
        recognizer.addKeyphraseSearch(WAKEUP_SEARCH, ACTIVATION_KEYPHRASE);
        recognizer.addNgramSearch(ACTION_SEARCH, new File(assetsDir, "predefined.lm.bin"));
    }
}
