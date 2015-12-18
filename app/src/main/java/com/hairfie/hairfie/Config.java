package com.hairfie.hairfie;

import android.net.Uri;

/**
 * Created by stephh on 24/11/15.
 */
public class Config {

    public String getAPIRoot() {
        return "http://api.hairfie.com/v1.2/";
    }
    public int getHairfiePixelSize() {
        return 500;
    }
    public String getGoogleAnalyticsTrackingId() {
        return "UA-55125713-5";
    }

    private static class Debug extends Config {
        public String getAPIRoot() {
            return "http://api-staging.hairfie.com/v1.2/";
        }
    }

    public static final Config instance = new Config();//Debug();
}
