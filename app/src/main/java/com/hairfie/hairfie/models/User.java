package com.hairfie.hairfie.models;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

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

    public Call logout(final Callbacks.SimpleCallback callback) {
        String accessToken = getAccessToken();

        if (null == accessToken)
            return null;


        SharedPreferences.Editor editor = Application.getInstance().getSharedPreferences().edit();
        editor.remove("user.accesstoken");
        editor.remove("user.userid");
        editor.commit();

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "users/logout")
                .post(RequestBody.create(HttpClient.JSON_MEDIA_TYPE, ""))
                .header("Authorization", accessToken)
                .build();
        Call result = HttpClient.getInstance().newCall(request);

        result.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (null != callback)
                    callback.onComplete(new Callbacks.Error(e));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (null != callback)
                        callback.onComplete(null);
                } else {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        Callbacks.Error error = new Callbacks.Error(json.getJSONObject("error"));
                        if (null != callback)
                            callback.onComplete(error);
                    } catch (JSONException e) {
                        if (null != callback)
                            callback.onComplete(new Callbacks.Error(e));
                    }
                }
            }
        });
        return result;

    }

    public Call login(@NonNull String email, @NonNull String password, @Nullable final Callbacks.SingleObjectCallback<User> callback) {
        try {
            JSONObject parameters = new JSONObject();
            parameters.put("email", email);
            parameters.put("password", password);
            Request request = new Request.Builder()
                    .url(Config.instance.getAPIRoot() + "users/login")
                    .post(RequestBody.create(HttpClient.JSON_MEDIA_TYPE, parameters.toString()))
                    .build();

            Call result = HttpClient.getInstance().newCall(request);
            result.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    if (null != callback)
                        callback.onComplete(null, new Callbacks.Error(e));
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            String accessToken = json.getString("id");
                            String userId = json.getString("userId");
                            if (null != accessToken && null != userId) {
                                setCredentials(accessToken, userId);
                            }

                            if (null != callback)
                                callback.onComplete(User.this, null);
                        } else {

                            Callbacks.Error error = new Callbacks.Error(json.getJSONObject("error"));
                            if (null != callback)
                                callback.onComplete(null, error);
                        }

                    } catch (JSONException e) {
                        if (callback != null)
                            callback.onComplete(null, new Callbacks.Error(e));
                    }


                }
            });
            return result;

        } catch (JSONException e) {
            Log.e(Application.TAG, "Error logging in", e);
            return null;
        }
    }

}
