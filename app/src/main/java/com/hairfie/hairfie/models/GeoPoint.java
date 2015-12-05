package com.hairfie.hairfie.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stephh on 04/12/15.
 */
public class GeoPoint implements Parcelable {
    public GeoPoint() {

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
}
