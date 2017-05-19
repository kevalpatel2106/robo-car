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

import android.support.annotation.NonNull;
import android.util.Log;

import com.kevalpatel2106.robocar.things.Controller;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.WebSocket;
import fi.iki.elonen.WebSocketFrame;

/**
 * Created by Keval on 19-May-17.
 */

class Socket extends WebSocket {
    private static final String TAG = Socket.class.getSimpleName();
    private final Controller mController;


    Socket(@NonNull NanoHTTPD.IHTTPSession handshakeRequest,
           @NonNull Controller controller) {
        super(handshakeRequest);
        mController = controller;
    }

    @Override
    protected void onPong(WebSocketFrame webSocketFrame) {
        Log.d(TAG, "onPong: Ping received.");
    }

    @Override
    protected void onMessage(WebSocketFrame webSocketFrame) {
        String command = webSocketFrame.getTextPayload();
        Log.d(TAG, "onMessage: WebSocket Command ->" + command);

        switch (command) {
            case RoboCommands.MOVE_FORWARD:
                mController.moveForward();
                break;
            case RoboCommands.MOVE_REVERSE:
                mController.moveReverse();
                break;
            case RoboCommands.TURN_RIGHT:
                mController.turnRight();
                break;
            case RoboCommands.TURN_LEFT:
                mController.turnLeft();
                break;
            case RoboCommands.TAKE_PIC:
                mController.captureImage();
                break;
            case RoboCommands.STOP:
                mController.stop();
                break;
        }
    }

    @Override
    protected void onClose(WebSocketFrame.CloseCode closeCode, String s, boolean b) {
        Log.d(TAG, "onClose: Socket closed.");
    }

    @Override
    protected void onException(IOException e) {
        Log.d(TAG, "onException: " + e.getMessage());
    }
}
