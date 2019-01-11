package com.ambrosus.ambviewer.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAdapter {

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

    public static String dateToText(Date date){
        return date != null ? DEFAULT_DATE_FORMAT.format(date) : null;
    }

    public static Date dateFromText(String date){
        try {
            return !TextUtils.isEmpty(date) ? DEFAULT_DATE_FORMAT.parse(date) : null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
