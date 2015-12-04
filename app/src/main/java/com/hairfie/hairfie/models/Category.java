package com.hairfie.hairfie.models;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.*;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Application;
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
public class Category implements Parcelable {

    public static final String CATEGORIES_UPDATED_BROADCAST_INTENT = "CATEGORIES_UPDATED_BROADCAST_INTENT";

    public String id;
    public String name;
    public Picture picture;
    public int position;

    public Category() {

    }
    private static List<Category> sCachedCategories;

    public static Call fetchAll(final ResultCallback.Single<List<Category>> callback) {
        return fetchAll(callback, false);
    }
    public static Call fetchAll(final ResultCallback.Single<List<Category>> callback, boolean ignoreCache) {


        if (sCachedCategories != null && !ignoreCache) {
            callback.executeOnOriginalThread(sCachedCategories, null);
            return null;
        }

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "categories")
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        ResultCallback.Single<List<Category>> wrapperCallback = new ResultCallback.Single<List<Category>>() {
            @Override
            public void onComplete(@Nullable List<Category> object, @Nullable ResultCallback.Error error) {
                if (null != object && null == error && !object.equals(sCachedCategories)) {
                    sCachedCategories = object;
                    // Send a broadcast
                    Intent intent = new Intent(CATEGORIES_UPDATED_BROADCAST_INTENT);
                    Application.getBroadcastManager().sendBroadcast(intent);
                }
                if (null != callback)
                    callback.executeOnOriginalThread(object, error);
            }
        };
        result.enqueue(wrapperCallback.okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<List<Category>>() {
        })));
        return result;

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Category) {
            Category other = (Category)o;
            return other.id.equals(id);
        }
        return super.equals(o);
    }

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<Category> CREATOR
            = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private Category(Parcel in) {
        Category other = Gson.sGson.fromJson(in.readString(), Category.class);
        id = other.id;
        name = other.name;
        picture = other.picture;
        position = other.position;
    }
}
