package com.hairfie.hairfie.models;

import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephh on 03/12/15.
 */
public class Hairfie {
    public String id;
    public Picture picture;
    public Price price;
    public Integer numLikes;
    public User.Profile author;
    public Picture[] pictures;

    public static Call latest(int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        try {
            JSONObject filter = new JSONObject();

            filter.put("limit", limit);
            filter.put("skip", skip);
            filter.put("order", "createdAt DESC");

            Request request = new Request.Builder()
                    .url(Config.instance.getAPIRoot() + "hairfies?filter="+ Uri.encode(filter.toString()))
                    .build();


            Call result = HttpClient.getInstance().newCall(request);
            result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Category>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<Hairfie>>(){})));
            return result;
        } catch (JSONException e) {
            callback.executeOnOriginalThread(null, new ResultCallback.Error(e));
            return null;
        }



    }
}
