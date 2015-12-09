package com.hairfie.hairfie.models;

import android.location.*;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hairfie.hairfie.Application;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by stephh on 04/12/15.
 */
public class GeoPoint implements Parcelable {
    public GeoPoint() {

    }
    public GeoPoint(android.location.Address address) {
        lat = address.getLatitude();
        lng = address.getLongitude();
    }
    public GeoPoint(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }
    public double lat;
    public double lng;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }

    public static final Parcelable.Creator<GeoPoint> CREATOR
            = new Parcelable.Creator<GeoPoint>() {
        public GeoPoint createFromParcel(Parcel in) {
            return new GeoPoint(in);
        }

        public GeoPoint[] newArray(int size) {
            return new GeoPoint[size];
        }
    };

    private GeoPoint(Parcel in) {
        GeoPoint other = Gson.sGson.fromJson(in.readString(), GeoPoint.class);
        this.lat = other.lat;
        this.lng = other.lng;
    }

    public Location toLocation() {
        Location result = new Location("");
        result.setLatitude(this.lat);
        result.setLongitude(this.lng);
        return result;
    }

    public static Geocoder search(final String where, final ResultCallback.Single<GeoPoint> callback) {
        final Geocoder geocoder = new Geocoder(Application.getInstance(), Locale.getDefault());

        Runnable executeInBackground = new Runnable() {
            @Override
            public void run() {
                try {
                    List<android.location.Address> results = geocoder.getFromLocationName(where, 1);


                    Log.d(Application.TAG, String.format(Locale.ENGLISH, "Geocoding results %s", results.size() > 0 ? results.get(0).toString() : null));
                    GeoPoint geoPoint = results.size() > 0 ? new GeoPoint(results.get(0)) : null;
                    callback.executeOnOriginalThread(geoPoint, null);
                } catch (IOException e) {
                    callback.executeOnOriginalThread(null, new ResultCallback.Error(e));
                }
            }
        };
        AsyncTask.execute(executeInBackground);
        return geocoder;
    }
}
