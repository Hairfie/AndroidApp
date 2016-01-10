package com.hairfie.hairfie.models;

import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by stephh on 03/12/15.
 */
public class Price {
    private static final NumberFormat sCurrencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    static {
        sCurrencyFormat.setMaximumFractionDigits(0);
    }
    public String currency;
    public Double amount = 0.0d;
    public String localizedString() {
        Currency cur = Currency.getInstance(currency);
        if (null != cur) {
            return sCurrencyFormat.format(amount);
        }
        return String.format(Locale.getDefault(), "%.0f %s", amount, currency);
    }
}
