package com.kevalpatel2106.robocar.things;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.controller.BoardDefaults;
import com.kevalpatel2106.robocar.things.controller.MovementController;
import com.kevalpatel2106.robocar.things.ultrasonic.ProximityAlertListener;
import com.kevalpatel2106.robocar.things.ultrasonic.UltrasonicSensorDriver;
import com.kevalpatel2106.robocar.things.webserver.WebServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ProximityAlertListener {

    private UltrasonicSensorDriver mUltrasonicSensor;
    private MovementController mMovementController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            //Initialize the movement controller
            mMovementController = new MovementController(new PeripheralManagerService());

            //Make the ultrasonic sensor
            mUltrasonicSensor = new UltrasonicSensorDriver(BoardDefaults.getGPIOForFrontTrig(),
                    BoardDefaults.getGPIOForFrontEcho(),
                    this);

            //Start the web server
            new WebServer(mMovementController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProximityAlert() {
        if (mMovementController == null) return;

        mMovementController.forceReverse();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMovementController.stop();
            }
        }, 400);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mUltrasonicSensor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
