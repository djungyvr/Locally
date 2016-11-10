package com.example.djung.locally.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Andy Lin on 2016-11-08.
 */

public class DateUtils {
    public static String parseHours(String input){
        String result = "";
        String[] daysOfTheWeek = new String[]{"Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays"};
        String[] dailyHours = input.split(",");
        for (int i = 0; i < dailyHours.length; i++){
            if (dailyHours[i].equals("00:00-00:00")){
                continue;
            }
            else {
                result = result + daysOfTheWeek[i] + " " + dailyHours[i] + "\n";
            }
        }
        return result;
    }

    /**
     *  Returns the current date and time in Vancouver as a string with the specified format
     *
     * @param format date and time pattern string
     * @return
     */
    public static String getCurrentDateAndTime(String format) {
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("America/Vancouver"));
        return df.format(Calendar.getInstance(
                TimeZone.getTimeZone("America/Vancouver"), Locale.CANADA).getTime());
    }
}
