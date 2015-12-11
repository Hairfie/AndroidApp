package com.hairfie.hairfie;

import android.net.Uri;

/**
 * Created by stephh on 24/11/15.
 */
public class Config {

    public String getAPIRoot() {
        return "http://api.hairfie.com/api/";
    }
    private static class Debug extends Config {
        public String getAPIRoot() {
            return "http://api-staging.hairfie.com/api/";
        }
    }

    public static final Config instance = new Debug();
}
