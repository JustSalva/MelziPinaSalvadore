package com.shakk.travlendar;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtility {

    /**
     * @param dateString A string of a date in the format "yyyy-MM-dd" to be transformed.
     * @return A long representing a date in the UTC format.
     */
    public static long getDateFromString(String dateString) {
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            date = new Date();
        }
        return date.getTime();
    }


    /**
     * @param date Date to be transformed.
     * @return A string of a date in the format "yyyy-MM-dd".
     */
    public static String getStringFromDate(Date date) {
        String dateString;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateString = sdf.format(date);
        return dateString;
    }

    // TODO seconds to hh:mm
    public static String fromUTCtoLocalTime(String UTCString) {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = utcFormat.parse("2012-08-15T22:56:02.038Z");
        } catch (ParseException e) {
            date = new Date();
        }

        SimpleDateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        pstFormat.setTimeZone(TimeZone.getTimeZone("PST"));
        return pstFormat.format(date);
    }


    /**
     * Translates a HH:mm string in unix time (UTC time zone).
     * @param time String in HH:mm format.
     * @return Unix time.
     */
    public static int fromHHmmToSeconds(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        try {
            calendar.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return (int) (calendar.getTimeInMillis() / 1000L);
        //return Integer.parseInt(sdf.format(date));
    }

    /**
     * Translates a Unix time (UTC time zone) in a HH:mm string.
     * @param seconds Unix time.
     * @return String in HH:mm format.
     */
    public static String fromSecondsToHHmm(int seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date(seconds * 1000L));
        calendar.setTimeZone(TimeZone.getDefault());
        return sdf.format(calendar.getTime());
    }
}
