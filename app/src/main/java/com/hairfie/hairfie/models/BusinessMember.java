package com.hairfie.hairfie.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public Integer numHairfies = 0;
    public Picture picture;

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
        if (null != picture)
            return picture;
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
    public String getAbbreviatedName() {
        List<String> tokens = new ArrayList<String>();
        if (null != firstName)
            tokens.add(firstName);
        if (null != lastName && lastName.length() > 0 && !" ".contentEquals(lastName))
            tokens.add(String.format(Locale.ENGLISH, "%s.", lastName.substring(0, 1)).toUpperCase(Locale.getDefault()));

        return StringUtils.join(tokens, " ");
    }


    public static Call activeInBusiness(Business business, ResultCallback.Single<List<BusinessMember>> callback) {
        if (null == business)
            return null;
        String url = Config.instance.getAPIRoot() + "businessMembers?filter[where][businessId]="+business.id+"&filter[where][hidden]=false&filter[where][active]=true";
        Request request = new Request.Builder()
                .url(url)
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<BusinessMember>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<BusinessMember>>(){})));
        return result;
    }

}
