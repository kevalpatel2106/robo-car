package com.kevalpatel2106.robocar.things.webserver;

import android.util.Log;

import com.kevalpatel2106.common.Commands;
import com.kevalpatel2106.robocar.things.MovementController;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class WebServer extends NanoHTTPD {
    private final MovementController mMovementController;

    public WebServer(MovementController movementController) throws IOException {
        super(8080);
        mMovementController = movementController;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.d("Server", "Start");
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {
            Log.d("HTTP request: ", session.getMethod() + " " + session.getUri());
            switch (session.getUri()) {
                case "/command":
                    Map<String, String> params = session.getParms();
                    Log.d("WebServer", "serve: " + params.get("movement"));

                    switch (params.get("movement")) {
                        case Commands.MOVE_FORWARD:
                            mMovementController.moveForward();
                            return newFixedLengthResponse("{\"s\":\"Ok\"}");
                        case Commands.MOVE_REVERSE:
                            mMovementController.moveReverse();
                            return newFixedLengthResponse("{\"s\":\"Ok\"}");
                        case Commands.TURN_RIGHT:
                            mMovementController.turnRight();
                            return newFixedLengthResponse("{\"s\":\"Ok\"}");
                        case Commands.TURN_LEFT:
                            mMovementController.turnLeft();
                            return newFixedLengthResponse("{\"s\":\"Ok\"}");
                        case Commands.STOP:
                            mMovementController.stop();
                            return newFixedLengthResponse("{\"s\":\"Ok\"}");
                    }
                    break;
            }
        }
        return newFixedLengthResponse("{\"s\":\"Failed\"}");
    }
}
