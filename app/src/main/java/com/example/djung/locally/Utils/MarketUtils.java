package com.example.djung.locally.Utils;

import android.location.Location;

import com.example.djung.locally.Model.Market;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Angy Chung on 2016-11-09.
 */

public class MarketUtils {

    /**
     *  Check if market is currently open at this date and time (Vancouver time zone)
     * @param market
     * @return whether or not market is currently open
     */
     public static boolean isMarketCurrentlyOpen(Market market){
        String currDate = DateUtils.getCurrentDateAndTime("MMdd HHmm");
        return isMarketOpenAtThisTime(market, currDate);
     }

    /**
     * Check if market is currently open at this date and time (Vancouver time zone)
     * @param datesOpen  dates of the year that the market is open, given in the format of "DD/MM-DD/MM" where
     *                  first pair of days and months is the opening date and the second pair is the closing date
     * @param dailyHours    daily hours of the market, given in the format of
     *                      "HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM"
     *                      where each comma separated string is the daily hours of that particular day, starting from Monday and ending on Sunday.
     * @return
     */
    public static boolean isMarketCurrentlyOpen(String datesOpen, String dailyHours){
        String currDate = DateUtils.getCurrentDateAndTime("MMdd HHmm");
        return isMarketOpenAtThisTime(datesOpen, dailyHours, currDate);
    }

    /**
     *  Check if market is open given a certain date and time represented by a string
     * @param market
     * @param weekdayDateTime date and time pattern string of the form "u MMdd HHmm" where
     *                        u = weekday (Monday = 1, ..., Sunday = 7)
     *                        MMdd = Monday(1-12) and day(1-31)
     *                        HHmm = hour(0-23) and minute (0-59)
     * @return
     */
    public static boolean isMarketOpenAtThisTime(Market market, String weekdayDateTime) {
        return isMarketOpenAtThisTime(market.getYearOpen(), market.getDailyHours(), weekdayDateTime);
    }

    /**
     * Check if market is open given a certain date and time represented by a string
     * @param yearOpen  dates of the year that the market is open, given in the format of "DD/MM-DD/MM" where
     *                  first pair of days and months is the opening date and the second pair is the closing date
     * @param dailyHours    daily hours of the market, given in the format of
     *                      "HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM,HH:MM-HH:MM"
     *                      where each comma separated string is the daily hours of that particular day, starting from Monday and ending on Sunday.
     * @param weekdayDateTime date and time pattern string of the form "u MMdd HHmm" where
     *                        u = weekday (Monday = 1, ..., Sunday = 7)
     *                        MMdd = Monday(1-12) and day(1-31)
     *                        HHmm = hour(0-23) and minute (0-59)
     * @return
     */
    public static boolean isMarketOpenAtThisTime(String yearOpen, String dailyHours, String weekdayDateTime) {
        String dates[] = weekdayDateTime.split(" ");

        // check if market is open during this time of year
        int dayInYear = Integer.parseInt(dates[1]);
        // parse string
        String[] daysOpen = yearOpen.split("[/-]");
        // convert dates to an int so we can compare them
        int dayOpen = Integer.parseInt(daysOpen[1]) * 100 + Integer.parseInt(daysOpen[0]);
        int dayClosed = Integer.parseInt(daysOpen[3]) * 100 + Integer.parseInt(daysOpen[2]);
        // if not open this time of year, no need to check daily hours
        if(dayInYear < dayOpen || dayInYear > dayClosed) return false;

        int dayOfWeek = Integer.parseInt(dates[0]);
        int time = Integer.parseInt(dates[2]);

        String[] hoursDays = (dailyHours.replace(":","")).split("[,-]");

        // find daily hours for specific day of the week
        int open = Integer.parseInt(hoursDays[dayOfWeek*2-2]);
        int close = Integer.parseInt(hoursDays[dayOfWeek*2-1]);

        if(open == 0 && close == 0) return false;    // market not open today
        return (time >= open && time <= close);
    }



    /**
     * Determines which markets are closest to the given location
     * @param markets a list of markets
     * @param location given location
     * @return a list of the markets in closest->farthest order
     *          empty list if no markets provided
     */
    public static List<Market> getClosestMarkets(List<Market> markets, Location location) {
        return getClosestMarkets(markets, location.getLatitude(), location.getLongitude());
    }

    /**
     * Determines which markets are closest to the given latitude and longitude
     * @param markets a list of markets
     * @param latitude
     * @param longitude
     * @return  a lit of the markets in closest->farthest order, empty list if no markets provided
     */
    public static List<Market> getClosestMarkets(List<Market> markets, double latitude, double longitude ) {
        List<Market> resultsList = new ArrayList<>();

        if(markets.isEmpty())
            return resultsList;

        List<Object[]> tempList = new ArrayList<>();

        for(Market m : markets) {
            float dist = getDistanceFromMarket(m, latitude, longitude);
            tempList.add(new Object[]{m, dist});
        }

        Collections.sort(tempList, new Comparator<Object []>() {
            public int compare(Object[] o1, Object[] o2) {
                return Float.compare((Float)o1[1], (Float)o2[1]);
            }
        });

        for(int i=0; i < tempList.size(); ++i) {
            resultsList.add((Market) tempList.get(i)[0]);
        }

        return resultsList;
    }

    /**
     * Returns the distance in metres between a market and location
     * @param market
     * @param location
     * @return distance between in metres
     */
    public static float getDistanceFromMarket( Market market, Location location) {
        return getDistanceFromMarket(market, location.getLatitude(), location.getLongitude());
    }

    /**
     * Returns the distance in metres between a market and given latitude and longitude
     * @param market
     * @param latitude
     * @param longitude
     * @return distance between in metres
     */
    public static float getDistanceFromMarket( Market market, double latitude, double longitude) {
        float[] dist = new float[1];
        Location.distanceBetween(market.getLatitude(), market.getLongitude(),
                latitude, longitude, dist);
        return dist[0];
    }
}
