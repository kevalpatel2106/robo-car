package com.kevalpatel2106.robocar.things.webserver;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kevalpatel2106.common.RoboCommands;
import com.kevalpatel2106.robocar.things.controller.MovementController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Keval Patel on 14/05/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class WebServer extends NanoHTTPD {
    private static final String TAG = WebServer.class.getSimpleName();

    @NonNull
    private final MovementController mMovementController;
    @NonNull
    private final AssetManager mAssetManager;

    public WebServer(@NonNull MovementController movementController,
                     @NonNull AssetManager assetManager) throws IOException {
        super(8080);

        mMovementController = movementController;
        mAssetManager = assetManager;

        //Start the server
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Log.d(TAG, "WebServer: Starting server.");
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {

            try {
                switch (session.getUri()) {
                    case "/command":
                        Map<String, String> params = session.getParms();

                        Log.d(TAG, "serve: " + params.get("movement"));

                        switch (params.get("movement")) {
                            case RoboCommands.MOVE_FORWARD:
                                mMovementController.moveForward();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.MOVE_REVERSE:
                                mMovementController.moveReverse();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.TURN_RIGHT:
                                mMovementController.turnRight();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.TURN_LEFT:
                                mMovementController.turnLeft();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                            case RoboCommands.STOP:
                                mMovementController.stop();
                                return newFixedLengthResponse("{\"s\":\"Ok\"}");
                        }
                        break;
                    default:
                        return getHTMLResponse("home.html");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newFixedLengthResponse("{\"s\":\"Failed\"}");
    }

    @NonNull
    private Response getHTMLResponse(@NonNull String assetName) throws IOException {
        InputStream inputStream = mAssetManager.open(assetName);
        return newFixedLengthResponse(Response.Status.OK, "text/html", inputStream, inputStream.available());
    }
}
