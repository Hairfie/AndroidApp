package com.hairfie.hairfie.models;

import com.squareup.okhttp.Call;

/**
 * Created by stephh on 14/12/15.
 */
public class Tag {
    public String id;
    public String name;
    public int position;
    public TagCategory category;
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
