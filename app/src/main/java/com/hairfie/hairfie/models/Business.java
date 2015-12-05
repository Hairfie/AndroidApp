package com.hairfie.hairfie.models;

import android.net.Uri;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by stephh on 04/12/15.
 */
public class Business {
    public String id;
    public User.Profile owner;
    public String kind;
    public String name;
    public GeoPoint gps;
    public String phoneNumber;
    public Address address;
    public Picture[] pictures;
    public Picture thumbnail;
    public Integer numHairfies;
    public Integer numReviews;
    public Float rating;
    public FacebookPage facebookPage;

    public static Call listNearby(GeoPoint geoPoint, String query, List<Category> categories, int limit, ResultCallback.Single<List<Business>> callback) {

        //https://hairfie.herokuapp.com/v1/businesses/search?pageSize=10&location[lat]=48.9021449&location][lng]=2&radius=100000&query=franck&facetFilters[categorySlugs][0]=barbier&facetFilters[categorySlugs][1]=coiffures

        StringBuilder builder = new StringBuilder();
        builder.append(String.format(Locale.ENGLISH, "radius=100000&pageSize=%d", limit));

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
        String url = Config.instance.getAPIRoot() + "businesses/nearby?"+ builder.toString();
        Log.d(Application.TAG, url);
        Request request = new Request.Builder()
                .url(url)
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Business>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<Business>>(){})));
        return result;
    }

}