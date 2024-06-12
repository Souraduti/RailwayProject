package com.railway.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String dateToFormattedString(Date date) {
        if (date == null) {
            return null;
        }
        return simpleDateFormat.format(date);
    }

    public static String longToFormattedString(long date) {
        if (date <= 0) {
            return null;
        }
        return simpleDateFormat.format(date);
    }
    public static Date toDate(String dateString){
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
