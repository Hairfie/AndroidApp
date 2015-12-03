package com.hairfie.hairfie.models;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by stephh on 25/11/15.
 */
public abstract class ResultCallback {
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

    public interface JSONObjectDeserializer<T> {
        public T deserialize(JSONObject json) throws Exception;
    }

    public interface StringDeserializer<T> {
        public T deserialize(String s) throws Exception;
    }

    public static abstract class Single<T> {
        private Handler mHandler;
        public Single() {
            mHandler = new Handler();
        }

        public void executeOnOriginalThread(@Nullable final T object, @Nullable final Error error) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onComplete(object, error);
                }
            });
        }

        public abstract void onComplete(@Nullable T object, @Nullable Error error);

        public Callback okHttpCallback(final StringDeserializer<T> deserializer) {
            return new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Single.this.executeOnOriginalThread(null, new Error(e));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    try {
                        if (response.isSuccessful()) {
                            Single.this.executeOnOriginalThread(deserializer.deserialize(body), null);
                        } else {

                            // Try to deserialize a json error
                            try {
                                JSONObject errorJson = new JSONObject(body);
                                ResultCallback.Error error = new ResultCallback.Error(errorJson.getJSONObject("error"));
                                Single.this.executeOnOriginalThread(null, error);
                                return;

                            } catch (JSONException e) {
                                // Ignore
                            }

                            // Fallback to plain text
                            ResultCallback.Error error = new ResultCallback.Error(body);
                            Single.this.executeOnOriginalThread(null, error);
                        }
                    } catch (Exception e) {
                        Single.this.executeOnOriginalThread(null, new Error(e));
                        return;
                    }

                }
            };
        }
        public Callback okHttpCallback(final JSONObjectDeserializer<T> deserializer) {
            return new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Single.this.executeOnOriginalThread(null, new Error(e));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String body = response.body().string();
                    try {
                        JSONObject json = new JSONObject(body);
                        if (response.isSuccessful()) {
                            Single.this.executeOnOriginalThread(deserializer.deserialize(json), null);
                        } else {
                            ResultCallback.Error error = new ResultCallback.Error(json.getJSONObject("error"));
                            Single.this.executeOnOriginalThread(null, error);
                        }
                    } catch (Exception e) {
                        Single.this.executeOnOriginalThread(null, new Error(e));
                        return;
                    }

                }
            };
        }
    }

    public static class Void<T> extends Single<T> {

        @Override
        public void onComplete(@Nullable T object, @Nullable Error error) {

        }
    }

}
