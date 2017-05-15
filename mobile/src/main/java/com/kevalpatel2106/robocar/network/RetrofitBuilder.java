package com.kevalpatel2106.robocar.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Keval on 20-Dec-16.
 * Build the client for api service.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class RetrofitBuilder {

    /**
     * Get the instance of the retrofit {@link APIService}.
     *
     * @return {@link APIService}
     */
    public static APIService getApiService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        //Building retrofit
        final Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(APIService.BASE_URL)
                .client(client)
                .build();

        return retrofit.create(APIService.class);
    }
}
