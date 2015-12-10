package com.hairfie.hairfie.models;

import java.util.Locale;

/**
 * Created by stephh on 04/12/15.
 */
public class Address {
    public String city;
    public String street;
    public String zipCode;
    public String country;

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s\n%s %s", street, zipCode, city);

    }
}
