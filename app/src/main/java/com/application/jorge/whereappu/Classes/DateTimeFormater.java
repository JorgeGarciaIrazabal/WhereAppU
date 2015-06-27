package com.application.jorge.whereappu.Classes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jorge on 25/06/2015.
 */
public class DateTimeFormater {
    public static final String dateFormat ="yyyy/MM/dd";
    public static final String timeFormat ="HH:mm";
    public static final String dateTimeFormat = "yyyy/MM/dd HH:mm";

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
    public static final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormat, Locale.getDefault());
    public static final SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(dateTimeFormat, Locale.getDefault());

    public static String toTime(Date date){
        return simpleTimeFormat.format(date);
    }
    public static String toDate(Date date){
        return simpleDateFormat.format(date);
    }
    public static String toDateTime(Date date){
        return simpleDateTimeFormat.format(date);
    }
}
