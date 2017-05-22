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
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.kevalpatel2106.robocar.things.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.IWebSocketFactory;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebSocket;
import fi.iki.elonen.WebSocketResponseHandler;

/**
 * Created by Keval Patel on 14/05/17.
 * This is a small web server running on your Raspberry PI. Connect to the local ip of the
 * raspberry Pi with port 8080 and issue the commands.
 *
 * @author Keval {https://github.com/kevalpatel2106}
 * @see 'https://github.com/NanoHttpd/nanohttpd'
 */

public final class WebServer extends NanoHTTPD implements SocketWriter {
    private static final String TAG = WebServer.class.getSimpleName();

    @NonNull
    private final Controller mController;
    @NonNull
    private final AssetManager mAssetManager;

    private Socket mSocket;
    private WebSocketResponseHandler mResponseHandler;


    /**
     * Start the web server.
     *
     * @param controller   {@link Controller} to control the movement.
     * @param assetManager {@link AssetManager} to load html wepages from assets.
     * @throws IOException If failed to initialize.
     */
    public WebServer(@NonNull Controller controller,
                     @NonNull AssetManager assetManager) throws IOException {
        super(8080);
        mController = controller;
        mAssetManager = assetManager;

        //Create socket
        mResponseHandler = new WebSocketResponseHandler(new IWebSocketFactory() {

            @Override
            public WebSocket openWebSocket(IHTTPSession handshake) {
                mSocket = new Socket(handshake, mController);
                return mSocket;
            }
        });

        //Start the server
        start();
        Log.d(TAG, "WebServer: Starting server.");
    }

    /**
     * @return {@link SocketWriter}.
     */
    public SocketWriter getSocketWriter() {
        return this;
    }

    /**
     * Handle the HTML request.
     */
    @Override
    public Response serve(IHTTPSession session) {
        NanoHTTPD.Response ws = mResponseHandler.serve(session);
        if (ws == null) {
            String uri = session.getUri();
            try {
                switch (uri) {
                    case "/":
                        return getHTMLResponse("home.html");
                    case "/css/style.css":
                        InputStream inputStream = mAssetManager.open("css/style.css");
                        return new NanoHTTPD.Response(Response.Status.OK, "text/css", inputStream);
                    case "/script/script.js":
                        inputStream = mAssetManager.open("script/script.js");
                        return new NanoHTTPD.Response(Response.Status.OK, "text/javascript", inputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ws;
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
        return new NanoHTTPD.Response(Response.Status.OK, "text/html", inputStream);
    }

    /**
     * Write the bitmap on the socket. This will be displayed in "IMG" tag of the website.
     *
     * @param image bitmap to display.
     */
    @Override
    public void writeImage(@NonNull Bitmap image) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            writeMessage("data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT));
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the text on the socket.
     * It is advisable to make this method synchronised.
     *
     * @param msg String message to write on socket
     */
    @Override
    public synchronized void writeMessage(@Nullable final String msg) {
        if (msg == null || mSocket == null) return;
        try {
            mSocket.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
