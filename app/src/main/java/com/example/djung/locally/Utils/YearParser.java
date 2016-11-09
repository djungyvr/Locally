package com.example.djung.locally.Utils;
import java.text.DateFormatSymbols;
/**
 * Created by Anna on 08.11.2016.
 */

public class YearParser {
    public static String parseYear(String yearOpen) {

        String[] datenmonth=yearOpen.split("[\\/\\-]");
        System.out.print(datenmonth[0]);
        String date1=datenmonth[0];
        String month1=month_InttoString(datenmonth[1]);
        String date2=datenmonth[2];
        String month2=month_InttoString(datenmonth[3]);

        return month1 + " " +date1+ " - "+month2 +" "+date2;

    }

    public static String month_InttoString(String month){
        String monthString = new DateFormatSymbols().getMonths()[Integer.valueOf(month)-1];
        return monthString;
    }

}