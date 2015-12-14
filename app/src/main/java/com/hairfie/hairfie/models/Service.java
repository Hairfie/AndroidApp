package com.hairfie.hairfie.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by stephh on 14/12/15.
 */
public class Service {
    public String businessId;
    public Date createdAt;
    public int durationMinutes;
    public String id;
    public boolean isManClassicPrice;
    public boolean isWomanClassicPrice;
    public String label;
    public Price price;
    public Date updatedAt;
}
