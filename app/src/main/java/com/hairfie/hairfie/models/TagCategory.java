package com.hairfie.hairfie.models;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.hairfie.hairfie.Application;
import com.hairfie.hairfie.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by stephh on 17/12/15.
 */
public class TagCategory {
    private static List<TagCategory> sCachedCategories;

    public transient List<Tag> tags = new ArrayList<>();
    public String id;
    public String name;
    public int position;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TagCategory))
            return super.equals(o);

        TagCategory other = (TagCategory) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static Call ordered(ResultCallback.Single<List<TagCategory>> callback) {
        return ordered(callback, false);
    }

    public static Call ordered(final ResultCallback.Single<List<TagCategory>> callback, boolean ignoreCache) {
        if (sCachedCategories != null && !ignoreCache) {
            callback.executeOnOriginalThread(sCachedCategories, null);
            return null;
        }

        Request request = new Request.Builder()
                .url(Config.instance.getAPIRoot() + "tags")
                .build();

        Call result = HttpClient.getInstance().newCall(request);
        ResultCallback.Single<List<Tag>> wrapperCallback = new ResultCallback.Single<List<Tag>>() {
            @Override
            public void onComplete(@Nullable List<Tag> object, @Nullable ResultCallback.Error error) {
                if (null != object && null == error) {

                    HashSet<TagCategory> categorySet = new HashSet<>();

                    // We'll rebuild Tag.sCachedTags
                    Tag.sCachedTags.clear();

                    for (Tag tag : object) {
                        Tag.sCachedTags.add(tag);

                        if (null == tag.category)
                            continue;

                        categorySet.add(tag.category);
                        for (TagCategory category : categorySet) {
                            if (category.equals(tag.category))
                                category.tags.add(tag);
                        }

                    }

                    // Order tags
                    for (TagCategory category : categorySet) {
                        Collections.sort(category.tags, new Comparator<Tag>() {
                            @Override
                            public int compare(Tag lhs, Tag rhs) {
                                if (lhs.position < rhs.position)
                                    return -1;
                                if (lhs.position > rhs.position)
                                    return 1;
                                return 0;
                            }
                        });
                    }

                    // Order categories
                    List<TagCategory> categories = new ArrayList<>(categorySet);

                    Collections.sort(categories, new Comparator<TagCategory>() {
                        @Override
                        public int compare(TagCategory lhs, TagCategory rhs) {
                            if (lhs.position < rhs.position)
                                return -1;
                            if (lhs.position > rhs.position)
                                return 1;
                            return 0;
                        }
                    });
                    sCachedCategories = categories;
                }
                if (null != callback)
                    callback.executeOnOriginalThread(sCachedCategories, error);
            }
        };
        result.enqueue(wrapperCallback.okHttpCallback(new ResultCallback.GsonDeserializer(new TypeToken<List<Tag>>() {
        })));
        return result;
    }
}
