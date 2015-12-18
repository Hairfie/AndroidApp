package com.hairfie.hairfie.models;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by stephh on 27/11/15.
 */
public class Picture {

    public static final String HAIRFIES_CONTAINER = "hairfies";
    public static final String USERS_CONTAINER = "users";

    public String name;
    public String container;
    public String url;
    public Picture(String name, String container) {

        this.name = name;
        this.container = container;
    }

    public static Call upload(@NonNull File file, String container, @Nullable final ResultCallback.Single<Picture> callback) {

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
        result.enqueue(callback == null ? null : callback.okHttpCallback(new ResultCallback.JSONObjectDeserializer<Picture>() {
            @Override
            public Picture fromJSONObject(JSONObject json) throws Exception {
                String container = null, id = null;

                JSONObject fileJson = json.optJSONObject("file");
                if (null != fileJson) {
                    id = fileJson.optString("id");
                    container = fileJson.optString("container");
                }

                return null != id && null != container ? new Picture(id, container) : null;
            }
        }));

        return result;
    }
}
