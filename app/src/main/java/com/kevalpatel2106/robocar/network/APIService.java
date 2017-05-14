package com.kevalpatel2106.robocar.network;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Keval on 20-Dec-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public interface APIService {
    String BASE_URL = "http://192.168.0.106:8080/";

    @GET("/command")
    Observable<CommandResponse> sendCommand(@Query("movement") String command);
}
