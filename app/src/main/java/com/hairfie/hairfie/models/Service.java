package com.hairfie.hairfie.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by stephh on 14/12/15.
 */
public class Service {
    public String businessId;
    public Date createdAt;
    public int durationMinutes = 0;
    public String id;
    public boolean isManClassicPrice;
    public boolean isWomanClassicPrice;
    public String label;
    public Price price;
    public Date updatedAt;

    public static Call forBusiness(Business business, ResultCallback.Single<List<Service>>callback) {
        String url = Config.instance.getAPIRoot() + "businessServices?filter[where][businessId]="+ business.id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new ResultCallback.Void<ArrayList<Service>>() : callback).okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<ArrayList<Service>>(){})));
        return result;
    }
}
