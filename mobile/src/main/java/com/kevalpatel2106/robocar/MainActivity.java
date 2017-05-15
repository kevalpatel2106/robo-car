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

package com.kevalpatel2106.robocar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.kevalpatel2106.common.RoboCommands;
import com.kevalpatel2106.robocar.network.CommandResponse;
import com.kevalpatel2106.robocar.network.RetrofitBuilder;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, BeaconConsumer {
    private static final int REQ_CODE_LOCATION_PERMISSION = 3654;

    @BindView(R.id.btn_forward)
    TextView mBtnForward;

    @BindView(R.id.btn_reverse)
    TextView mBtnReverse;

    @BindView(R.id.btn_left)
    TextView mBtnLeft;

    @BindView(R.id.btn_right)
    TextView mBtnRight;

    @BindView(R.id.tv_distance)
    TextView mDistanceTv;

    private BeaconManager mBeaconManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        enableBluetooth();

        //Touch listener
        mBtnForward.setOnTouchListener(this);
        mBtnLeft.setOnTouchListener(this);
        mBtnRight.setOnTouchListener(this);
        mBtnReverse.setOnTouchListener(this);

        //Initiate beacon manager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initBeaconRanging();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_CODE_LOCATION_PERMISSION);
        }
    }

    private void initBeaconRanging() {
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initBeaconRanging();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBeaconManager != null) mBeaconManager.unbind(this);
    }

    private void sendCommandApiRequest(@RoboCommands.Command String command) {
        final Observable<CommandResponse> observable = RetrofitBuilder
                .getApiService()
                .sendCommand(command);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<CommandResponse>() {
                    @Override
                    public void onCompleted() {
                        //Do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CommandResponse observer) {

                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.btn_forward:
                        sendCommandApiRequest(RoboCommands.MOVE_FORWARD);
                        break;
                    case R.id.btn_reverse:
                        sendCommandApiRequest(RoboCommands.MOVE_REVERSE);
                        break;
                    case R.id.btn_left:
                        sendCommandApiRequest(RoboCommands.TURN_LEFT);
                        break;
                    case R.id.btn_right:
                        sendCommandApiRequest(RoboCommands.TURN_RIGHT);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                sendCommandApiRequest(RoboCommands.STOP);
                break;
        }
        return true;
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("DefaultLocale")
                        @Override
                        public void run() {
                            mDistanceTv.setText(String.format("Distance : %.2f meters",
                                    beacons.iterator().next().getDistance()));
                        }
                    });
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void enableBluetooth() {
        BluetoothAdapter adapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        if (!adapter.isEnabled()) adapter.enable();
    }
}
