package com.hairfie.hairfie;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hairfie.hairfie.models.HttpClient;
import com.hairfie.hairfie.models.User;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by stephh on 24/11/15.
 */
public class Application extends android.app.Application {
    public static final String TAG = "Hairfie";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    @NonNull
    private static Application sInstance;

    @NonNull
    public static Application getInstance() {
        return sInstance;
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

}
