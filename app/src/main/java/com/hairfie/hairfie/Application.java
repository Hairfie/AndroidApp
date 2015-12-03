package com.hairfie.hairfie;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.User;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import pl.aprilapps.easyphotopicker.EasyImage;
import com.squareup.picasso.OkHttpDownloader;
/**
 * Created by stephh on 24/11/15.
 */
public class Application extends android.app.Application {
    public static final String TAG = "Hairfie";
    private static Picasso sPicasso;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        sPicasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(this, 128 * 1024 * 1024))
                .build();
        sPicasso.setIndicatorsEnabled(true);
        sPicasso.setLoggingEnabled(true);
        Picasso.setSingletonInstance(sPicasso);



        // Easy image configuration (for profile pictures)
        EasyImage.configuration(this)
                .saveInAppExternalFilesDir();

        // Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Fetch current user profile
        User.getCurrentUser().fetchProfile(new ResultCallback.Single<User>() {
            @Override
            public void onComplete(@Nullable User user, @Nullable ResultCallback.Error error) {

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

    public static Picasso getPicasso() {
        return sPicasso;
    }

}
