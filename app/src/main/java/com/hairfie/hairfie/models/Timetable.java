package com.hairfie.hairfie.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by stephh on 10/12/15.
 */
public class Timetable implements Parcelable {
    @SerializedName("MON")
    public TimeWindow[] monday;
    @SerializedName("TUE")
    public TimeWindow[] tuesday;
    @SerializedName("WED")
    public TimeWindow[] wednesday;
    @SerializedName("THU")
    public TimeWindow[] thursday;
    @SerializedName("FRI")
    public TimeWindow[] friday;
    @SerializedName("SAT")
    public TimeWindow[] saturday;
    @SerializedName("SUN")
    public TimeWindow[] sunday;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<Timetable> CREATOR
            = new Parcelable.Creator<Timetable>() {
        public Timetable createFromParcel(Parcel in) {
            return Gson.sGson.fromJson(in.readString(), Timetable.class);
        }

        public Timetable[] newArray(int size) {
            return new Timetable[size];
        }
    };



    public boolean isOpenToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        TimeWindow[] windows;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                windows = sunday;
                break;
            case Calendar.MONDAY:
                windows = monday;
                break;
            case Calendar.TUESDAY:
                windows = tuesday;
                break;
            case Calendar.WEDNESDAY:
                windows = wednesday;
                break;
            case Calendar.THURSDAY:
                windows = thursday;
                break;
            case Calendar.FRIDAY:
                windows = friday;
                break;
            case Calendar.SATURDAY:
                windows = saturday;
                break;
            default:
                return false;
        }
        return windows.length > 0;
    }
}
