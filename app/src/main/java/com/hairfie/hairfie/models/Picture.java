package com.hairfie.hairfie.models;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by stephh on 27/11/15.
 */
public class Picture {

    String mId;
    String mContainer;
    public Picture(String id, String container) {

        mId = id;
        mContainer = container;
    }

    public static Call upload(@NonNull File file, String container, @Nullable final Callbacks.ObjectCallback<Picture> callback) {

        String mimeType = Application.getInstance().getContentResolver().getType(Uri.fromFile(file));
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"file\""),
                        RequestBody.create(null, file))
                .build();

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "containers/"+container+"/upload")
                .post(requestBody)
                .build();

        Call result = HttpClient.getInstance().newCall(request);
        result.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (null != callback)
                    callback.onCompleteWrapper(null, new Callbacks.Error(e));
            }

            @Override
            public void onResponse(Response response) throws IOException {

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        String container = null, id = null;

                        JSONObject result = json.optJSONObject("result");
                        if (null != result) {
                            JSONObject files = result.optJSONObject("files");
                            if (null != files) {
                                JSONObject fileJson = files.optJSONObject("file");
                                if (null != fileJson) {
                                    id = fileJson.optString("name");
                                    container = fileJson.optString("container");
                                }
                            }
                        }

                        Picture picture = null != id && null != container ? new Picture(id, container) : null;
                        if (null != callback)
                            callback.onCompleteWrapper(picture, null);
                    } else {

                        Callbacks.Error error = new Callbacks.Error(json.getJSONObject("error"));
                        if (null != callback)
                            callback.onCompleteWrapper(null, error);
                    }

                } catch (JSONException e) {
                    if (callback != null)
                        callback.onCompleteWrapper(null, new Callbacks.Error(e));
                }


            }
        });
        return result;
    }

    public JSONObject toJson() throws  JSONException {
        JSONObject result = new JSONObject();
        result.put("id", mId);
        result.put("container", mContainer);
        return result;
    }
}
