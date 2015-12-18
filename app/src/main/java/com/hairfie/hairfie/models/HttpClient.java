package com.hairfie.hairfie.models;

import android.util.Log;

import com.hairfie.hairfie.Application;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by stephh on 25/11/15.
 */
public class HttpClient extends OkHttpClient {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static HttpClient sIntance = new HttpClient();
    public static  HttpClient getInstance() {
        return sIntance;
    }

    private HttpClient() {
        super();
        setConnectTimeout(60, TimeUnit.SECONDS);
        setReadTimeout(60, TimeUnit.SECONDS);
        setWriteTimeout(60, TimeUnit.SECONDS);
        interceptors().add(new AccessTokenInterceptor());

    }

    private class AccessTokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Log.d(Application.TAG, "Calling "+request.urlString());

            String accessToken = User.getCurrentUser().getAccessToken();
            if (accessToken == null) {
                return chain.proceed(request);
            }

            Log.d(Application.TAG, "Adding Authorization Header: "+ accessToken);
            Request newRequest;
            newRequest = request.newBuilder()
                    .addHeader("Authorization", accessToken)
                    .build();
            return chain.proceed(newRequest);
        }
    }
}
