package com.hairfie.hairfie.models;

import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;

/**
 * Created by stephh on 03/12/15.
 */
public class Gson {
    static final com.google.gson.Gson sGson = new GsonFireBuilder().dateSerializationPolicy(DateSerializationPolicy.rfc3339).createGson();
}
