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

package com.kevalpatel2106.robocar.things.server;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kevalpatel2106.common.EndPoints;
import com.kevalpatel2106.common.RoboCommands;
import com.kevalpatel2106.robocar.things.Controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Keval Patel on 14/05/17.
 * This is a small web server running on your Raspberry PI. Connect to the local ip of the
 * raspberry Pi with port 8080 and issue the commands.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 * @see 'https://github.com/NanoHttpd/nanohttpd'
 */

public final class WebServer extends NanoHTTPD {
    private static final String TAG = WebServer.class.getSimpleName();

    @NonNull
    private final Controller mController;
    @NonNull
    private final AssetManager mAssetManager;

    /**
     * Start the web server.
     *
     * @param controller {@link Controller} to control the movement.
     * @param assetManager       {@link AssetManager} to load html wepages from assets.
     * @throws IOException If failed to initialize.
     */
    public WebServer(@NonNull Controller controller,
                     @NonNull AssetManager assetManager) throws IOException {
        super(8080);

        mController = controller;
        mAssetManager = assetManager;

        //Start the server
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.d(TAG, "WebServer: Starting server.");
    }

    /**
     * Handle the HTML request.
     */
    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {

            try {
                switch (session.getUri()) {
                    case "/" + EndPoints.ENDPOINT_COMMAND:    //Command to control the robot
                        Map<String, String> params = session.getParms();

                        Log.d(TAG, "serve: New command = " + params.get(EndPoints.PARAM_COMMAND));

                        switch (params.get(EndPoints.PARAM_COMMAND)) {
                            case RoboCommands.MOVE_FORWARD:
                                mController.moveForward();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.MOVE_REVERSE:
                                mController.moveReverse();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.TURN_RIGHT:
                                mController.turnRight();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.TURN_LEFT:
                                mController.turnLeft();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.TAKE_PIC:
                                //TODO take pic command.
                                return getHTMLResponse("home.html");
                            case RoboCommands.STOP:
                                mController.stop();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                        }
                        break;
                    case "/" + EndPoints.ENDPOINT_FILE:
                        params = session.getParms();
                        FileInputStream fis = null;
                        try {
                            Log.d(TAG, "serve: File name = " + params.get(EndPoints.PARAM_COMMAND));

                            fis = new FileInputStream(Environment.getExternalStorageDirectory()
                                    + "/" + params.get(EndPoints.PARAM_FILE));
                            return newChunkedResponse(Response.Status.OK, "image/jpg", fis);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return newFixedLengthResponse("");
                        } finally {
                            if (fis != null) fis.close();
                        }
                    case "/" + EndPoints.ENDPOINT_ROOT:
                    default:    //Load up the website.
                        return getHTMLResponse("home.html");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newFixedLengthResponse("{\"s\":\"Failed\"}");
    }

    /**
     * Get the HTML webpage from the assets folder and return the fixed length {@link fi.iki.elonen.NanoHTTPD.Response}.
     *
     * @param assetName name of the HTML asset file.
     * @return Fixed length {@link fi.iki.elonen.NanoHTTPD.Response}
     * @throws IOException If asset not found or failed to read asset file
     */
    @NonNull
    private Response getHTMLResponse(@NonNull String assetName) throws IOException {
        InputStream inputStream = mAssetManager.open(assetName);
        return newFixedLengthResponse(Response.Status.OK, "text/html", inputStream, inputStream.available());
    }
}
