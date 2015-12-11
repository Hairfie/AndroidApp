package com.hairfie.hairfie.models;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephh on 11/12/15.
 */
public class Review {
    public String id;
    public User.Profile author;
    public String firstName;
    public String lastName;
    public Float rating;
    public String comment;
    public String createdAt;

    public static Call fetch(Business business, int limit, int skip, ResultCallback.Single<List<Review>> callback) {
        JsonObject where = new JsonObject();
        if (business != null) {
            where.addProperty("businessId", business.id);
        }
        return fetch(where, limit, skip, callback);
    }

    public static Call fetch(JsonElement where, int limit, int skip, ResultCallback.Single<List<Review>> callback) {

        JsonObject filter = new JsonObject();
        filter.addProperty("limit", limit);
        filter.addProperty("skip", skip);
        filter.addProperty("order", "createdAt DESC");
        if (null != where)
            filter.add("where", where);

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "businessreviews?filter="+ Uri.encode(filter.toString()))
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Category>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<Review>>(){})));
        return result;
    }
}
