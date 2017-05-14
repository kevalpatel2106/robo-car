package com.kevalpatel2106.robocar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.kevalpatel2106.common.Commands;
import com.kevalpatel2106.robocar.network.CommandResponse;
import com.kevalpatel2106.robocar.network.RetrofitBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.btn_forward)
    TextView mBtnForward;

    @BindView(R.id.btn_reverse)
    TextView mBtnReverse;

    @BindView(R.id.btn_left)
    TextView mBtnLeft;

    @BindView(R.id.btn_right)
    TextView mBtnRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Touch listener
        mBtnForward.setOnTouchListener(this);
        mBtnLeft.setOnTouchListener(this);
        mBtnRight.setOnTouchListener(this);
        mBtnReverse.setOnTouchListener(this);
    }

    private void sendCommandApiRequest(String command) {
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
                        sendCommandApiRequest(Commands.MOVE_FORWARD);
                        break;
                    case R.id.btn_reverse:
                        sendCommandApiRequest(Commands.MOVE_REVERSE);
                        break;
                    case R.id.btn_left:
                        sendCommandApiRequest(Commands.TURN_LEFT);
                        break;
                    case R.id.btn_right:
                        sendCommandApiRequest(Commands.TURN_RIGHT);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                sendCommandApiRequest(Commands.STOP);
                break;
        }
        return true;
    }
}
