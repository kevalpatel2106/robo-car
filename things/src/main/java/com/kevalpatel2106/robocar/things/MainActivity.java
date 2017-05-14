package com.kevalpatel2106.robocar.things;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.things.pio.PeripheralManagerService;
import com.kevalpatel2106.robocar.things.webserver.WebServer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            MovementController movementController = new MovementController(new PeripheralManagerService());
            movementController.stop();  //Reset
            WebServer webServer = new WebServer(movementController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
