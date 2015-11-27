package com.hairfie.hairfie.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by stephh on 25/11/15.
 */
public abstract class Callbacks {
    public static class Error {
        @NonNull
        public String message;

        @Nullable
        public String code;

        public int status;

        public transient Throwable cause;
        public Error(JSONObject json) throws JSONException {
            message = json.getString("message");
            code = json.optString("code");
            status = json.getInt("status");
        }
        public Error(Throwable cause) {
            this.cause = cause;
            this.message = cause.getLocalizedMessage();
        }

        public Error(String message) {
            this.message = message;
        }
    }
    public interface SimpleCallback {
        public void onComplete(@Nullable Error error);
    }

    public interface SingleObjectCallback<T> {
        public void onComplete(@Nullable T object, @Nullable Error error);
    }

    public interface ListCallback<T> {
        public void onComplete(@Nullable List<T> object, @Nullable Error error);
    }
}
