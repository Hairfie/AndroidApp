package com.hairfie.hairfie.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by stephh on 03/12/15.
 */
public class Hairfie implements Parcelable {
    public String id;
    public Picture picture;
    public Price price;
    public Integer numLikes;
    public User.Profile author;
    public Picture[] pictures;
    public boolean displayBusiness;
    public Business business;
    public String landingPageUrl;
    public Tag[] tags;

    public List<Tag> orderedTags() {
        List<Tag> list = Arrays.asList(tags);
        Collections.sort(list, new Comparator<Tag>() {
            @Override
            public int compare(Tag lhs, Tag rhs) {
                if (lhs.position < rhs.position)
                    return -1;
                if (lhs.position > rhs.position)
                    return 1;
                return 0;
            }
        });
        return list;
    }
    public static Call latest(int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        return latest((JSONObject)null, limit, skip, callback);
    }

    public static Call latest(BusinessMember businessMember, int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        JSONObject where = new JSONObject();
        if (null != businessMember)
            try {
                where.put("businessMemberId", businessMember.id);
            } catch (JSONException e) {
                Log.e(Application.TAG, "Could not create JSON", e);
                return null;
            }
        return latest(where, limit, skip, callback);
    }
    public static Call latest(Business business, int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        JSONObject where = new JSONObject();
        if (null != business)
            try {
                where.put("businessId", business.id);
            } catch (JSONException e) {
                Log.e(Application.TAG, "Could not create JSON", e);
                return null;
            }
        return latest(where, limit, skip, callback);
    }

    private static Call latest(JSONObject where, int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        try {
            JSONObject filter = new JSONObject();

            filter.put("limit", limit);
            filter.put("skip", skip);
            filter.put("order", "createdAt DESC");
            if (null != where)
                filter.put("where", where);

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

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(Gson.sGson.toJson(this));
    }
    public static final Parcelable.Creator<Hairfie> CREATOR
            = new Parcelable.Creator<Hairfie>() {
        public Hairfie createFromParcel(Parcel in) {
            return Gson.sGson.fromJson(in.readString(), Hairfie.class);
        }

        public Hairfie[] newArray(int size) {
            return new Hairfie[size];
        }
    };
}
