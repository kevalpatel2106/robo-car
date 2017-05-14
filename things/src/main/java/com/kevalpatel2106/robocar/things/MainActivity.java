package com.kevalpatel2106.robocar.things;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.beacon.BeaconCreator;
import com.kevalpatel2106.robocar.things.controller.MovementController;
import com.kevalpatel2106.robocar.things.webserver.WebServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MovementController mMovementController;
    private BeaconCreator mBeaconCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Initialize the movement controller
            mMovementController = new MovementController(new PeripheralManagerService());

            //Start as a beacon
            mBeaconCreator = new BeaconCreator(this);
            mBeaconCreator.initBeaconTransmission();

            //Start the web server
            new WebServer(mMovementController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconCreator.stopBeaconTransmission();
        try {
            mMovementController.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
