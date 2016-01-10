package com.hairfie.hairfie.models;

import android.util.Log;

import com.hairfie.hairfie.Application;
import com.squareup.okhttp.Call;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Created by stephh on 14/12/15.
 */
public class Tag {
    protected static Set<Tag> sCachedTags = new HashSet<>();
    public static Tag findById(String id) {
        if (null == id)
            return null;

        for (Tag tag : sCachedTags) {
            if (null != tag.id && id.contentEquals(tag.id))
                return tag;
        }
        return null;
    }

    public String id;
    public String name;
    public int position = 0;
    public TagCategory category;

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "<Tag name=%s>", name);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag))
            return super.equals(o);

        Tag other = (Tag) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
