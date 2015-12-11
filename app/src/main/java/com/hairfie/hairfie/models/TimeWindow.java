package com.hairfie.hairfie.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stephh on 10/12/15.
 */
public class TimeWindow implements Parcelable {

    public String startTime;
    public String endTime;
    public String appointmentMode;
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<TimeWindow> CREATOR
            = new Parcelable.Creator<TimeWindow>() {
        public TimeWindow createFromParcel(Parcel in) {
            return Gson.sGson.fromJson(in.readString(), TimeWindow.class);
        }

        public TimeWindow[] newArray(int size) {
            return new TimeWindow[size];
        }
    };

}
