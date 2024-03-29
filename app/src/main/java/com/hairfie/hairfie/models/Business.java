package com.hairfie.hairfie.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by stephh on 04/12/15.
 */
public class Business implements Parcelable {
    public String id;
    public String kind;
    public String name;
    public GeoPoint gps;
    public String phoneNumber;
    public Address address;
    public Picture[] pictures;
    public Integer numHairfies = 0;
    public Integer numReviews = 0;
    public Float rating = 0.0f;
    public Timetable timetable;
    public String accountType;

    public static Call listSimilar(Business business, ResultCallback.Single<List<Business>>callback) {
        if (null == business)
            return null;

        String url = Config.instance.getAPIRoot() + "businesses/"+ business.id + "/similar";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Business>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<Business>>(){})));
        return result;
    }

    public static Call ownedBy(User.Profile profile, ResultCallback.Single<List<Business>>callback) {
        if (null == profile || null == profile.id) {
            return null;
        }

        String url = Config.instance.getAPIRoot() + "users/"+ profile.id + "/managed-businesses";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Business>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<Business>>(){})));
        return result;
    }

    public static Call listNearby(GeoPoint geoPoint, String query, List<Category> categories, int limit, int skip, ResultCallback.Single<BusinessSearchResults> callback) {

        //https://hairfie.herokuapp.com/v1/businesses/search?pageSize=10&location[lat]=48.9021449&location][lng]=2&radius=100000&query=franck&facetFilters[categorySlugs][0]=barbier&facetFilters[categorySlugs][1]=coiffures

        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ENGLISH, "radius=100000&limit=%d&skip=%d", limit, skip));

        if (null != geoPoint)
            builder.append(String.format(Locale.ENGLISH, "&location[lat]=%f&location[lng]=%f", geoPoint.lat, geoPoint.lng));

        if (null != query)
            builder.append("&query=" + Uri.encode(query));

        if (null != categories) {
            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                builder.append(String.format(Locale.ENGLISH, "&facetFilters[categorySlugs]=%s", Uri.encode(category.name)));
            }
        }
        String url = Config.instance.getAPIRoot() + "businesses/search?"+ builder.toString();
        Request request = new Request.Builder()
                .url(url)
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<BusinessSearchResults>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<BusinessSearchResults>(){})));
        return result;
    }

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<Business> CREATOR
            = new Parcelable.Creator<Business>() {
        public Business createFromParcel(Parcel in) {
            return Gson.sGson.fromJson(in.readString(), Business.class);
        }

        public Business[] newArray(int size) {
            return new Business[size];
        }
    };


    public boolean isPremium() {
        return "PREMIUM".equals(accountType);
    }

}
