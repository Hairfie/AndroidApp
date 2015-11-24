package com.hairfie.hairfie;

import android.support.annotation.NonNull;

import com.hairfie.hairfie.models.User;
import com.strongloop.android.loopback.RestAdapter;

/**
 * Created by stephh on 24/11/15.
 */
public class Application extends android.app.Application {

    private static Application sInstance;

    @NonNull
    private RestAdapter mRestAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        configureLoopback();
        sInstance = this;
    }

    public static Application getInstance() {
        return sInstance;
    }

    @NonNull
    public RestAdapter getRestAdapter() {
        return mRestAdapter;
    }
    private void configureLoopback() {
        mRestAdapter = new RestAdapter(this, Config.instance.getAPIRoot());
        User.initialize(mRestAdapter);
    }
}
