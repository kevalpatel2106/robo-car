package com.kevalpatel2106.robocar.things.server;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kevalpatel2106.common.RoboCommands;
import com.kevalpatel2106.robocar.things.chassis.MovementController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Keval Patel on 14/05/17.
 * This is a small web server running on your Raspberry PI. Connect to the local ip of the
 * raspberry Pi with port 8080 and issue the commands.
 *
 * @see 'https://github.com/NanoHttpd/nanohttpd'
 * @author Keval {https://github.com/kevalpatel2106}
 */

public final class WebServer extends NanoHTTPD {
    private static final String TAG = WebServer.class.getSimpleName();

    @NonNull
    private final MovementController mMovementController;
    @NonNull
    private final AssetManager mAssetManager;

    /**
     * Start the web server.
     *
     * @param movementController {@link MovementController} to control the movement.
     * @param assetManager       {@link AssetManager} to load html wepages from assets.
     * @throws IOException If failed to initialize.
     */
    public WebServer(@NonNull MovementController movementController,
                     @NonNull AssetManager assetManager) throws IOException {
        super(8080);

        mMovementController = movementController;
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
                    case "/command":    //Command to control the robot
                        Map<String, String> params = session.getParms();

                        Log.d(TAG, "serve: New command = " + params.get("cmdname"));

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
