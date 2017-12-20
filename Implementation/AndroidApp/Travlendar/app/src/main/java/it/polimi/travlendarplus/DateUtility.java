package it.polimi.travlendarplus;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtility {

    /**
     * @param dateString A string of a date in the format "yyyy-MM-dd" to be transformed.
     * @return A Calendar in the UTC format.
     */
    public static Calendar getCalendarFromString(String dateString) {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            calendar.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar;
    }


    /**
     * @param calendar Calendar in UTC time to be transformed.
     * @return A string of a date in the format "yyyy-MM-dd".
     */
    public static String getStringFromCalendar(Calendar calendar) {
        calendar.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sdf.format(calendar.getTime());
    }

    /**
     * Translates a HH:mm string in unix time (UTC time zone).
     * @param time String in HH:mm format.
     * @return Unix time.
     */
    public static long getSecondsFromHHmm(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        try {
            calendar.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return (calendar.getTimeInMillis() / 1000L);
    }

    /**
     * Translates a Unix time (UTC time zone) in a HH:mm string.
     * @param seconds Unix time.
     * @return String in HH:mm format.
     */
    public static String getHHmmFromSeconds(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date(seconds * 1000L));
        calendar.setTimeZone(TimeZone.getDefault());
        return sdf.format(calendar.getTime());
    }

    /**
     * Translates a Unix time (UTC time zone) in an Instant.toString()
     * @param seconds Unix time.
     * @return Instant.toString().
     */
    public static String getInstantFromSeconds(long seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date(seconds * 1000L));
        return sdf.format(calendar.getTime());
    }

    public static String getUTCHHmmFromLocalHHmm(String localString) {
        long seconds = getSecondsFromHHmm(localString);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date(seconds * 1000L));
        return sdf.format(calendar.getTime());
    }

    public static long getNoUTCSecondsFromHHmm(String time) {
        String[] array = time.split(":");
        long hours = Long.parseLong(array[0]);
        long minutes = Long.parseLong(array[1]);
        return hours * 3600 + minutes * 60;
    }
}
