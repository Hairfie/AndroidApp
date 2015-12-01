package com.hairfie.hairfie.models;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.hairfie.hairfie.R;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by stephh on 24/11/15.
 */
public class User {

    public static final String PROFILE_UPDATED_BROADCAST_INTENT = "PROFILE_UPDATED_BROADCAST_INTENT";

    private static final Gson sGson = new Gson();

    @NonNull
    private static final User sCurrentUser = new User();

    @NonNull
    public static User getCurrentUser() {
        return sCurrentUser;
    }

    public boolean isAuthenticated() {
        return getAccessToken() != null;
    }

    @Nullable
    public String getAccessToken() {
        return Application.getInstance().getSharedPreferences().getString("user.accesstoken", null);
    }

    @Nullable
    public String getUserId() {
        return Application.getInstance().getSharedPreferences().getString("user.userid", null);
    }



    private void setCredentials(@NonNull String accessToken, @NonNull String userId) {

        SharedPreferences.Editor editor = Application.getInstance().getSharedPreferences().edit();
        editor.putString("user.accesstoken", accessToken);
        editor.putString("user.userid", userId);
        editor.commit();
    }

    @Nullable
    public Profile getProfile() {
        String savedJson =  Application.getInstance().getSharedPreferences().getString("user.profile", null);
        if (null == savedJson)
            return null;

        return sGson.fromJson(savedJson, Profile.class);
    }



    private void setProfile(Profile profile) {
        String oldValue = Application.getInstance().getSharedPreferences().getString("user.profile", null);
        String newValue = profile == null ? null : sGson.toJson(profile);

        if (oldValue == null && newValue == null) {
            return;
        }
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }

        SharedPreferences.Editor editor = Application.getInstance().getSharedPreferences().edit();
        if (null == newValue) {
            editor.remove("user.profile");
        } else {
            editor.putString("user.profile", newValue);
        }
        editor.commit();

        // Send a broadcast
        Intent intent = new Intent(PROFILE_UPDATED_BROADCAST_INTENT);
        LocalBroadcastManager.getInstance(Application.getInstance()).sendBroadcast(intent);
    }

    public Call logout(final Callbacks.ObjectCallback<Void> callback) {
        String accessToken = getAccessToken();

        if (null == accessToken)
            return null;

        SharedPreferences.Editor editor = Application.getInstance().getSharedPreferences().edit();
        editor.remove("user.accesstoken");
        editor.remove("user.userid");
        editor.remove("user.profile");
        editor.commit();

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "users/logout")
                .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, ""))
                .header("Authorization", accessToken)
                .build();
        Call result = HttpClient.getInstance().newCall(request);

        result.enqueue((null == callback ? new Callbacks.EmptyCallback<Void>() : callback).okHttpCallback(new Callbacks.StringDeserializer<Void>() {
            @Override
            public Void deserialize(String s) throws Exception {
                return null;
            }
        }));

        return result;

    }

    public Call login(@NonNull String email, @NonNull String password, @Nullable final Callbacks.ObjectCallback<User> callback) {
        try {
            JSONObject parameters = new JSONObject();
            parameters.put("email", email);
            parameters.put("password", password);
            Request request = new Request.Builder()
                    .url(Config.instance.getAPIRoot() + "users/login")
                    .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, parameters.toString()))
                    .build();

            Call result = HttpClient.getInstance().newCall(request);
            result.enqueue(new FetchProfileWrapperCallback(null == callback ? new Callbacks.EmptyCallback<User>() : callback).okHttpCallback(new Callbacks.JSONObjectDeserializer<User>() {
                @Override
                public User deserialize(JSONObject json) throws JSONException {
                    String accessToken = json.getString("id");
                    String userId = json.getString("userId");
                    if (null != accessToken && null != userId) {
                        setCredentials(accessToken, userId);
                    }
                    return User.this;
                }
            }));


            return result;

        } catch (JSONException e) {
            Log.e(Application.TAG, "Error logging in", e);
            return null;
        }
    }



    public Call signup(JSONObject parameters, final Callbacks.ObjectCallback<User> callback) {

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "users/")
                .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, parameters.toString()))
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue(new FetchProfileWrapperCallback(null == callback ? new Callbacks.EmptyCallback<User>() : callback).okHttpCallback(new Callbacks.JSONObjectDeserializer<User>() {
            @Override
            public User deserialize(JSONObject json) throws Exception {
                String accessToken = null, userId = null;

                JSONObject accessTokenJSON = json.optJSONObject("accessToken");
                if (accessTokenJSON != null) {
                    accessToken = accessTokenJSON.optString("id");
                    userId = accessTokenJSON.optString("userId");
                }

                if (null != accessToken && null != userId) {
                    setCredentials(accessToken, userId);
                }

                return User.this;
            }
        }));

        return result;
    }

    public Call resetPassword(CharSequence email, final Callbacks.ObjectCallback<Void> callback) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("email", email);
        } catch (JSONException e) {
            Log.e(Application.TAG, "Could not reset password", e);
            return null;
        }

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "users/reset")
                .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, parameters.toString()))
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new Callbacks.EmptyCallback<Void>() : callback).okHttpCallback(new Callbacks.StringDeserializer<Void>() {
            @Override
            public Void deserialize(String s) throws Exception {
                return null;
            }
        }));

        return result;
    }

    public Call uploadPicture(File pictureFile, Callbacks.ObjectCallback<Picture> callback) {
        return Picture.upload(pictureFile, "users", callback);
    }

    public Call loginWithFacebook(AccessToken accessToken, final Callbacks.ObjectCallback<User> callback) {

        if (!accessToken.getPermissions().contains("email")) {
            if (null != callback)
                callback.onCompleteWrapper(null, new Callbacks.Error(Application.getInstance().getString(R.string.email_permission_required)));
            return null;
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("access_token", accessToken.getToken());
        } catch (JSONException e) {
            if (null != callback)
                callback.onCompleteWrapper(null, new Callbacks.Error(e));
            return null;
        }

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "auth/facebook/token/")
                .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, parameters.toString()))
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue(new FetchProfileWrapperCallback((null == callback ? new Callbacks.EmptyCallback<User>() : callback)).okHttpCallback(new Callbacks.JSONObjectDeserializer<User>() {
            @Override
            public User deserialize(JSONObject json) throws Exception {
                String accessToken = json.getString("id");
                String userId = json.getString("userId");
                if (null != accessToken && null != userId) {
                    setCredentials(accessToken, userId);
                }

                return User.this;
            }
        }));

        return result;

    }

    public Call fetchProfile(final Callbacks.ObjectCallback<User> callback) {
        String userId = getUserId();
        if (null == userId) {
            return null;
        }

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "users/" + Uri.encode(userId))
                .build();


        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue((null == callback ? new Callbacks.EmptyCallback<User>() : callback).okHttpCallback(new Callbacks.StringDeserializer<User>() {
            @Override
            public User deserialize(String s) throws Exception {
                Profile profile = sGson.fromJson(s, Profile.class);
                User.this.setProfile(profile);
                return User.this;
            }
        }));
        return result;

    }

    private static class FetchProfileWrapperCallback extends Callbacks.ObjectCallback<User> {

        Callbacks.ObjectCallback<User> mCallback;
        FetchProfileWrapperCallback(Callbacks.ObjectCallback<User> callback) {
            super();
            mCallback = callback;
        }
        @Override
        public void onComplete(@Nullable final User user, @Nullable Callbacks.Error error) {

            // Yield on error or missing user ID
            if (null != error || null == user.getUserId()) {
                if (null != mCallback)
                    mCallback.onCompleteWrapper(user, error);
                return;
            }

            user.fetchProfile(mCallback);
        }
    }

    // Example:
    // {"id":"23041225-f559-4626-9aa8-5d05025e219c","href":"http:\/\/api-staging.hairfie.com\/v1.1\/users\/23041225-f559-4626-9aa8-5d05025e219c","gender":"FEMALE","firstName":"Yjf","lastName":"Tjr","picture":{"container":"users","name":"db7bf3ce-dad2-4bb3-a4f5-ec83d3fff763","url":"http:\/\/d3fkjxim4dbfwn.cloudfront.net\/v1.1\/containers\/users\/download\/db7bf3ce-dad2-4bb3-a4f5-ec83d3fff763"},"numHairfies":0,"numBusinessReviews":0,"email":"e@e.com","locale":"fr","newsletter":true,"language":"fr"}

    public static class Profile {

        public String id;
        public String gender;
        public String firstName;
        public String lastName;
        public String email;
        public String locale;
        public String language;
        public boolean newsletter;
        public Integer numHairfies;
        public Integer numBusinessReviews;
        public Picture picture;

        public String toString() {
            return String.format(Locale.ENGLISH, "<User id=%s email=%s>", id, email);
        }

        public String getFullname() {
            List<String> tokens = new ArrayList<String>();
            if (null != firstName)
                tokens.add(firstName);
            if (null != lastName)
                tokens.add(lastName);

            return StringUtils.join(tokens, " ");
        }
    }


}
