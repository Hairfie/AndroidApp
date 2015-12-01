package com.hairfie.hairfie;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.hairfie.hairfie.models.Callbacks;
import com.hairfie.hairfie.models.HttpClient;
import com.hairfie.hairfie.models.User;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by stephh on 24/11/15.
 */
public class Application extends android.app.Application {
    public static final String TAG = "Hairfie";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // Easy image configuration (for profile pictures)
        EasyImage.configuration(this)
                .saveInAppExternalFilesDir();

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Fetch current user profile
        User.getCurrentUser().fetchProfile(new Callbacks.ObjectCallback<User>() {
            @Override
            public void onComplete(@Nullable User user, @Nullable Callbacks.Error error) {

                // TODO: a notification here that the profile has been updated
                User.Profile profile = user != null ? user.getProfile() : null;
                Log.d(Application.TAG, "Updated user profile: " + (profile != null ? profile.toString() : "null"));
            }
        });
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
