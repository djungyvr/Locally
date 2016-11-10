package com.example.djung.locally.Utils;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
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
     *              if market is closed on that day, it is represented as "00:00-00:00"
     * @return String showing weekday & time for when the market/vendor is open
     */
    public static String parseHours(String input){
        String result = "";
        String[] daysOfTheWeek = new String[]{"Mondays", "Tuesdays", "Wednesdays", "Thursdays",
                "Fridays", "Saturdays", "Sundays"};
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
     * Formats time range string into hyphenated 12-hour format with AM/PM markers
     * @param timeRange string of the form "HH:mm-HH:mm"
     * @return string of the form "h:mm a - h:mm a"
     */
    public static String formatTimeRangeString(String timeRange) {
        String[] times = timeRange.split("-");
        return formatTimeString(times[0]) + " - " + formatTimeString(times[1]);
    }

    /**
     *  Formats time strnig into 12-hour format with am/pm marker
     * @param time string of the form "HH:mm"
     * @return string of the form "h:mm a"
     */
    public static String formatTimeString(String time) {
        SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm", Locale.CANADA);
        SimpleDateFormat outFormat = new SimpleDateFormat("h:mm a", Locale.CANADA);
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
        DateFormat df = new SimpleDateFormat(format, Locale.CANADA);
        df.setTimeZone(TimeZone.getTimeZone("America/Vancouver"));
        return df.format(Calendar.getInstance(
                TimeZone.getTimeZone("America/Vancouver"), Locale.CANADA).getTime());
    }

    /**
     * Returns a string of the date range for when the market/vendor is open
     * @param yearOpen time of the year the market is open encoded in a string:
     *                  "MM/dd-MM/dd"
     * @return string of the form "MMMM d - MMMM d"
     */
    public static String parseYear(String yearOpen) {

        String[] datenmonth=yearOpen.split("[\\/\\-]");

        String date1 = String.valueOf(Integer.parseInt(datenmonth[0]));
        String month1=month_InttoString(datenmonth[1]);
        String date2 = String.valueOf(Integer.parseInt(datenmonth[2]));
        String month2=month_InttoString(datenmonth[3]);

        if(date1.equals(date2) && month1.equals(month2))
            return month1 + " " + date1;

        return month1 + " " +date1+ " - "+month2 +" "+date2;

    }

    /**
     * Returns string representation of month number
     * @param month 1-12 where 1=January, ..., 12=December
     * @return month text
     */
    public static String month_InttoString(String month){
        String monthString = new DateFormatSymbols().getMonths()[Integer.valueOf(month)-1];
        return monthString;
    }
}
