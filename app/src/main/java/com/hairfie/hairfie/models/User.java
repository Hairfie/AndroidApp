package com.hairfie.hairfie.models;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.gson.Gson;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.hairfie.hairfie.R;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by stephh on 24/11/15.
 */
public class User {
    private static Gson gson = new Gson();

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

    public Call logout(final Callbacks.ObjectCallback<Void> callback) {
        String accessToken = getAccessToken();

        if (null == accessToken)
            return null;


        SharedPreferences.Editor editor = Application.getInstance().getSharedPreferences().edit();
        editor.remove("user.accesstoken");
        editor.remove("user.userid");
        editor.commit();

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "users/logout")
                .post(RequestBody.create(HttpClient.MEDIA_TYPE_JSON, ""))
                .header("Authorization", accessToken)
                .build();
        Call result = HttpClient.getInstance().newCall(request);

        result.enqueue(null == callback ? null : callback.okHttpCallback(new Callbacks.StringDeserializer<Void>() {
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
            result.enqueue(callback == null ? null : callback.okHttpCallback(new Callbacks.JSONObjectDeserializer<User>() {
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
        result.enqueue(null == callback ? null : callback.okHttpCallback(new Callbacks.JSONObjectDeserializer<User>() {
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
        result.enqueue(null == callback ? null : callback.okHttpCallback(new Callbacks.StringDeserializer<Void>() {
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
        result.enqueue(null == callback ? null : callback.okHttpCallback(new Callbacks.JSONObjectDeserializer<User>() {
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

}
