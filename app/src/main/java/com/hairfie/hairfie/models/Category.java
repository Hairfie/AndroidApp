package com.hairfie.hairfie.models;

import android.net.Uri;

import com.google.gson.*;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephh on 01/12/15.
 */
public class Category {

    public String id;
    public String name;
    public Picture picture;
    public int position;

    public Category() {

    }
    public static Call fetchAll(ResultCallback.Single<List<Category>> callback) {


        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "categories")
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<List<Category>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<List<Category>>(){})));
        return result;

    }
}
