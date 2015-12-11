package com.hairfie.hairfie.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephh on 10/12/15.
 */
public class BusinessMember implements Parcelable {
    public String id;
    public boolean active;
    public User.Profile user;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public Integer numHairfies;

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<BusinessMember> CREATOR
            = new Parcelable.Creator<BusinessMember>() {
        public BusinessMember createFromParcel(Parcel in) {
            return Gson.sGson.fromJson(in.readString(), BusinessMember.class);
        }

        public BusinessMember[] newArray(int size) {
            return new BusinessMember[size];
        }
    };


    public Picture getPicture() {
        if (null != user)
            return user.picture;
        return null;
    }

    public String getFullname() {
        List<String> tokens = new ArrayList<String>();
        if (null != firstName)
            tokens.add(firstName);
        if (null != lastName)
            tokens.add(lastName);

        return StringUtils.join(tokens, " ");
    }

}
