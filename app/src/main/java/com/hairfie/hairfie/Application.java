package com.hairfie.hairfie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.hairfie.hairfie.models.Category;
import com.hairfie.hairfie.models.ResultCallback;
import com.hairfie.hairfie.models.User;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import pl.aprilapps.easyphotopicker.EasyImage;

import com.squareup.picasso.OkHttpDownloader;

/**
 * Created by stephh on 24/11/15.
 */
public class Application extends android.app.Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String LOCATION_UPDATED_BROADCAST_INTENT = "LOCATION_UPDATED_BROADCAST_INTENT";
    public static final String TAG = "Hairfie";
    private static Picasso sPicasso;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        sPicasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(this, 128 * 1024 * 1024))
                .build();
//        sPicasso.setIndicatorsEnabled(true);
//        sPicasso.setLoggingEnabled(true);
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

        // Prepare category list
        Category.fetchAll(null);

        // Google apis
        buildGoogleApiClient();
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

    public static LocalBroadcastManager getBroadcastManager() {
        return LocalBroadcastManager.getInstance(getInstance());
    }

    Location mLastLocation;

    public Location getLastLocation() {
        return mLastLocation;
    }

    public void updateLastLocation() {
        if (mConnectedToGoogleApi) {
            Location newLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            boolean updated;
            if (null == mLastLocation && null != newLastLocation)
                updated = true;
            else if (null != mLastLocation && null != newLastLocation && !newLastLocation.equals(mLastLocation))
                updated = true;
            else
                updated = false;

            if (null != newLastLocation)
                mLastLocation = newLastLocation;

            if (updated) {
                Log.d(TAG, "Location updated: "+newLastLocation.toString());
                getBroadcastManager().sendBroadcast(new Intent(LOCATION_UPDATED_BROADCAST_INTENT));
            }

        }
    }

    GoogleApiClient mGoogleApiClient;
    boolean mConnectedToGoogleApi;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnectedToGoogleApi = true;
        
        // Immediately request location to get a first fix
        if (null == mLastLocation)
            updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(Application.TAG, "Google APIs connection suspended");
        mConnectedToGoogleApi = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mConnectedToGoogleApi = false;
        CharSequence message = connectionResult.getErrorMessage();
        Log.d(Application.TAG, "Could not connect to Google APIs: " + (null != message ? message : "null"));
    }
}
