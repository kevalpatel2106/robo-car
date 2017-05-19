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

public final class WebServer extends NanoHTTPD implements CommandSender {
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

    public CommandSender getListner() {
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
                if (uri.equals("/")) return getHTMLResponse("home.html");
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

    @Override
    public void sendImage(Bitmap msg) {
        try {
            if (mSocket != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                msg.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                mSocket.send("data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String msg) {
        try {
            if (mSocket != null) mSocket.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
