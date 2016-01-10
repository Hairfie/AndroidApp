package com.hairfie.hairfie.models;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;


import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by stephh on 11/12/15.
 */
public class Review {
    public String id;
    public User.Profile author;
    public String firstName;
    public String lastName;
    public Float rating = 0.0f;
    public String comment;
    public Date createdAt;

    public String getAbbreviatedName() {
        List<String> tokens = new ArrayList<String>();
        if (null != firstName)
            tokens.add(firstName);
        if (null != lastName && lastName.length() > 0)
            tokens.add(String.format(Locale.ENGLISH, "%s.", lastName.substring(0, 1)).toUpperCase(Locale.getDefault()));

        return StringUtils.join(tokens, " ");
    }


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
