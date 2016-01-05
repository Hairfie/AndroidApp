package com.hairfie.hairfie.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;

import com.google.gson.JsonObject;
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    public Date createdAt;
    public BusinessMember businessMember;

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

    public static Call latest(List<Category> categories, int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        return latest(null, null, categories, limit, skip, callback);
    }

    public static Call latest(BusinessMember businessMember, List<Category> categories, int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        JSONObject where = new JSONObject();
        if (null != businessMember)
            try {
                where.put("businessMemberId", businessMember.id);
            } catch (JSONException e) {
                Log.e(Application.TAG, "Could not create JSON", e);
                return null;
            }
        return latest(null, businessMember, categories, limit, skip, callback);
    }

    public static Call latest(Business business, List<Category> categories, int limit, int skip, ResultCallback.Single<List<Hairfie>> callback) {
        JSONObject where = new JSONObject();
        if (null != business)
            try {
                where.put("businessId", business.id);
            } catch (JSONException e) {
                Log.e(Application.TAG, "Could not create JSON", e);
                return null;
            }
        return latest(business, null, categories, limit, skip, callback);
    }

    private static Call latest(Business business, BusinessMember businessMember, List<Category> categories, int limit, int skip, final ResultCallback.Single<List<Hairfie>> callback) {

        // Build the category string
        String tagString = "";
        if (null != categories) {
            Set<Tag> tags = new HashSet<>();
            for (Category category : categories)
                tags.addAll(category.getTags());

            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (Tag tag : tags) {
                if (i > 0)
                    sb.append("&");

                sb.append(Uri.encode(String.format(Locale.ENGLISH, "tags[%d]", i)));
                sb.append("=");
                sb.append(Uri.encode(tag.name));
                i++;

            }
            tagString = sb.toString();
        }

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot()
                        + "hairfies/search?"
                        + String.format(Locale.ENGLISH, "limit=%d&skip=%d", limit, skip)
                        + (business != null ? "&businessId=" + business.id : "")
                        + (businessMember != null ? "&businessMemberId=" + businessMember.id : "")
                        + "&" + tagString)
                .build();


        ResultCallback.Single<HairfieSearchResponse> wrapper = new ResultCallback.Single<HairfieSearchResponse>() {
            @Override
            public void onComplete(@Nullable HairfieSearchResponse object, @Nullable ResultCallback.Error error) {
                if (null != error) {
                    if (null != callback)
                        callback.executeOnOriginalThread(null, error);
                    return;

                }

                List<Hairfie> hairfies = new ArrayList<>();
                if (null != object) {
                    for (int i = 0; i < object.hits.length; i++)
                        hairfies.add(object.hits[i]);
                }
                if (null != callback)
                    callback.executeOnOriginalThread(hairfies, null);

            }
        };


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue(wrapper.okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<HairfieSearchResponse>() {
        })));
        return result;

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

    public static Call post(List<Picture> pictures, Price price, Business business, BusinessMember businessMember, CharSequence customerEmail, List<Tag> tags, ResultCallback.Single<Hairfie> callback) {
        JsonObject parameters = new JsonObject();
        if (null != pictures) {
            String[] ids = new String[pictures.size()];
            for (int i = 0; i < pictures.size(); i++)
                ids[i] = pictures.get(i).name;
            parameters.add("pictures", Gson.sGson.toJsonTree(ids));

        }
        if (null != price)
            parameters.add("price", Gson.sGson.toJsonTree(price));
        if (null != business)
            parameters.addProperty("businessId", business.id);
        else
            parameters.addProperty("selfMade", true);

        if (null != businessMember)
            parameters.addProperty("businessMemberId", businessMember.id);
        if (null != customerEmail)
            parameters.addProperty("customerEmail", customerEmail.toString());
        if (null != tags) {
            String[] names = new String[tags.size()];
            for (int i = 0; i < tags.size(); i++)
                names[i] = tags.get(i).id;
            parameters.add("tags", Gson.sGson.toJsonTree(names));
        }
        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "hairfies")
                .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, parameters.toString()))
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Category>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<Hairfie>() {
        })));
        return result;
    }
}
