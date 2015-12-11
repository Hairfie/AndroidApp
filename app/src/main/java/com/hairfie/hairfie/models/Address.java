package com.hairfie.hairfie.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by stephh on 04/12/15.
 */
public class Address implements Parcelable {
    public String city;
    public String street;
    public String zipCode;
    public String country;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s\n%s %s", street, zipCode, city);

    }


    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<Address> CREATOR
            = new Parcelable.Creator<Address>() {
        public Address createFromParcel(Parcel in) {
            return Gson.sGson.fromJson(in.readString(), Address.class);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
