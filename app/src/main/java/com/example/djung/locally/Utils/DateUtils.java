package com.example.djung.locally.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.TimeZone;

/**
 * Created by Andy Lin on 2016-11-08.
 */

public class DateUtils {

    /**
     * Returns a string of the days and times a market/vendor is open
     * @param input market/vendor's hours encoded in a string:
     *              hours for each day of the week separated by a comma,
     *              first represents Monday,
     *              if market is closed on that day, it is represented as 00:00-00:00
     * @return String showing weekday & time for when the market/vendor is open
     */
    public static String parseHours(String input){
        String result = "";
        String[] daysOfTheWeek = new String[]{"Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays"};
        String[] dailyHours = input.split(",");
        for (int i = 0; i < dailyHours.length; i++){
            if (dailyHours[i].equals("00:00-00:00")){
                continue;
            }
            else {
                result = result + daysOfTheWeek[i] + " " + formatTimeRangeString(dailyHours[i]) + "\n";
            }
        }
        return result;
    }

    /**
     * Formats time range string into hyphenated 12-hour format with am/pm markers
     * @param timeRange string of the form HH:mm-HH:mm
     * @return string of the form h:mm a - h:mm a
     */
    public static String formatTimeRangeString(String timeRange) {
        String[] times = timeRange.split("-");
        return formatTimeString(times[0]) + " - " + formatTimeString(times[1]);
    }

    /**
     *  Formats time strnig into 12-hour format with am/pm marker
     * @param time string of the form HH:mm
     * @return string of the form h:mm a
     */
    public static String formatTimeString(String time) {
        SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat outFormat = new SimpleDateFormat("h:mm a");
        String formattedStr = "";

        try {
            formattedStr = outFormat.format(inFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedStr;
    }


    /**
     *  Returns the current date and time in Vancouver as a string with the specified format
     *
     * @param format date and time pattern string
     * @return String representing the current date and time in the specified format
     */
    public static String getCurrentDateAndTime(String format) {
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("America/Vancouver"));
        return df.format(Calendar.getInstance(
                TimeZone.getTimeZone("America/Vancouver"), Locale.CANADA).getTime());
    }
}
