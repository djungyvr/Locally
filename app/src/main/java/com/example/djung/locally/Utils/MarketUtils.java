package com.example.djung.locally.Utils;

import android.location.Location;

import com.example.djung.locally.Model.Market;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Angy on 2016-11-09.
 */

public class MarketUtils {
    /**
     * Checks if market is currently open during the given hours
     *
     * @param dailyHours market's hours encoded in a string:
     *              hours for each day of the week separated by a comma,
     *              first represents Monday,
     *              if market is closed on that day, it is represented as 00:00-00:00
     *  @param yearOpen time of the year the market is open encoded in a string:
     *                  MM/dd-MM/dd
     * @return boolean if market is currently open
     */
    public static boolean isMarketCurrentlyOpen(String yearOpen, String dailyHours) {
        // get current weekday & time
        String currDate = DateUtils.getCurrentDateAndTime("u HHmm MMdd");
        // extract individual elements of the date
        String dates[] = currDate.split(" ");

        // check if market is open during this time of year
        int dayinyear = Integer.parseInt(dates[2]);

        // parse string
        String[] days_open = yearOpen.split("[/-]");
        // convert dates to an int so we can compare them
        int day_open = Integer.parseInt(days_open[1]) * 100 + Integer.parseInt(days_open[0]);
        int day_closed = Integer.parseInt(days_open[3]) * 100 + Integer.parseInt(days_open[2]);
        // if not open this time of year, no need to check daily hours
        if(dayinyear < day_open || dayinyear > day_closed) return false;

        int dayofweek = Integer.parseInt(dates[0]);
        int time = Integer.parseInt(dates[1]);

        String[] hours_days = (dailyHours.replace(":","")).split("[,-]");

        // find daily hours for specific day of the week
        int open = Integer.parseInt(hours_days[dayofweek*2-2]);
        int close = Integer.parseInt(hours_days[dayofweek*2-1]);

        if(open == 0 && close == 0) return false;    // market not open today
        return (time >= open && time <= close);
    }

    public static boolean isMarketCurrentlyOpen(Market market){
        return isMarketCurrentlyOpen( market.getYearOpen(), market.getDailyHours());
    }

    /**
     *  Determines which markets are closest to the given location
     * @param loc given location
     * @return a list of the markets in closest->farthest order
     *          empty list if no markets provided
     */
    public static List<Market> getClosestMarkets(List<Market> markets, Location loc) {
        List<Market> resultsList = new ArrayList<>();

        if(markets.isEmpty())
            return resultsList;

        List<Object[]> tempList = new ArrayList<>();

        for(Market m : markets) {
            float dist = getDistanceFromMarket(m, loc);
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
     *  Gets the distance in metres between a market and location
     * @param market
     * @param loc
     * @return distance between in metres
     */
    public static float getDistanceFromMarket( Market market, Location loc) {
        float[] dist = new float[1];
        Location.distanceBetween(market.getLatitude(), market.getLongitude(),
                loc.getLatitude(), loc.getLongitude(), dist);
        return dist[0];
    }
}
