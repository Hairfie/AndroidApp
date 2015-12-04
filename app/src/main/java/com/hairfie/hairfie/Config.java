package com.hairfie.hairfie;

import android.net.Uri;

/**
 * Created by stephh on 24/11/15.
 */
public class Config {

    public String getAPIRoot() {
        return "http://api.hairfie.com/api/";
    }
    public String getGoogleAPIKey() {
        return "";
    }
    private static class Debug extends Config {
        public String getAPIRoot() {
            return "http://api-staging.hairfie.com/api/";
        }
        public String getGoogleAPIKey() {
            return "AIzaSyAbX3wVdcLamLcB_QkmegusXGhUUu-swi4";
        }
    }

    public static final Config instance = new Debug();
}
