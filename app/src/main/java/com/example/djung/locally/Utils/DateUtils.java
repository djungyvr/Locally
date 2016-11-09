package com.example.djung.locally.Utils;

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
}
