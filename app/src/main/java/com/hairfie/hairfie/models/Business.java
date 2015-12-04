package com.hairfie.hairfie.models;

import android.util.Log;

import com.hairfie.hairfie.Application;
import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        HashMap<String,Object> parameters = new HashMap<>();
        if (null != geoPoint)
            parameters.put("here", geoPoint);
        if (null != query)
            parameters.put("query", query);
        if (null != categories && categories.size() > 0) {
            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories)
                categoryNames.add(category.name);

            HashMap<String, List<String>> facetFilters = new HashMap<>();
            facetFilters.put("categories", categoryNames);
            parameters.put("facetFilters", facetFilters);
        }

        return null;
    }

}
