package com.example.djung.locally;


import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.MarketUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Angy Chung on 2016-11-09.
 */

public class MarketUtilsUnitTest {
    private String mAlwaysClosedHours = "00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00";
    private String mAlwaysOpenYear = "01/01-31/12";
    private String mAlwaysOpenHours = "00:00-23:59,00:00-23:59,00:00-23:59,00:00-23:59,00:00-23:59,00:00-23:59,00:00-23:59";

    private MarketTest m1, m2, m3, m4;
    private List<Market> mMarketsList;
//    private double ubcNest_latitude = 49.266579;
//    private double ubcNest_longitude = -123.249809;

    @Before
    public void initialize() {
        m1 = new MarketTest();
        m1.setName("West End Farmers Market");
        m1.setLatitude(49.2824765);
        m1.setLongitude(-123.1307488);
        m1.setDailyHours("00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,09:00-14:00,00:00-00:00");
        m1.setYearOpen("28/05-22/10");

        m2 = new MarketTest();
        m2.setName("UBC Farmers Market");
        m2.setLatitude(49.2511064);
        m2.setLongitude(-123.2343889);
        m2.setDailyHours("00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,09:00-13:00,00:00-00:00");
        m2.setYearOpen("04/06-29/10");

        m3 = new MarketTest();
        m3.setName("Kitsilano Farmers Market");
        m3.setLatitude(49.2628916802915);
        m3.setLongitude(-123.1607234197085);
        m3.setDailyHours("00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,10:00-14:00");
        m3.setYearOpen("08/05-23/10");

        m4 = new MarketTest();

        mMarketsList = new ArrayList<>();
        mMarketsList.add(m1);
        mMarketsList.add(m2);
        mMarketsList.add(m3);

    }

    @Test
    public void testIsAlwaysClosedOrOpenMarketCurrentlyOpen() {
        m4.setYearOpen(mAlwaysOpenYear);
        m4.setDailyHours(mAlwaysClosedHours);
        boolean result1 = MarketUtils.isMarketCurrentlyOpen(m4);
        assertFalse(result1);
        m4.setDailyHours(mAlwaysOpenHours);
        boolean result2 = MarketUtils.isMarketCurrentlyOpen(m4);
        assertTrue(result2);
    }

    @Test
    public void testIsMarketOpenOnTodaysWeekday() {
        m4.setYearOpen(mAlwaysOpenYear);
        // get today's current day of the week
        int weekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String hours;

        switch(weekday) {
            case 1: // Monday
                hours = "00:00-23:59,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
            case 2: // Tuesday
                hours = "00:00-00:00,00:00-23:59,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
            case 3: // Wednesday
                hours = "00:00-00:00,00:00-00:00,00:00-23:59,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
            case 4: // Thursday
                hours = "00:00-00:00,00:00-00:00,00:00-00:00,000:00-23:59,00:00-00:00,00:00-00:00,00:00-00:00";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
            case 5: // Friday
                hours = "00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-23:59,00:00-00:00,00:00-00:00";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
            case 6: // Saturday
                hours = "00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-23:59,00:00-00:00";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
            case 7: // Sunday
                hours = "00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00,00:00-23:590";
                m4.setDailyHours(hours);
                assertTrue(MarketUtils.isMarketCurrentlyOpen(m4));
                break;
        }
    }

    @Test
    public void testIsMarketOpen() {
        String testTime1 = "1 0724 1400";        // Monday, July 24, 2:00 PM
        m4.setYearOpen("01/07-24/07");          // Open July 1 - July 24
        m4.setDailyHours("09:00-17:00,00:00-00:00,00:00-00:00," +
                "00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00"); // open Monday 9AM-5PM
        assertTrue(MarketUtils.isMarketOpenAtThisTime(m4, testTime1));

        String testTime2 = "1 0724 1700";        // Monday, July 24, 5:00 PM, at closing time
        assertTrue(MarketUtils.isMarketOpenAtThisTime(m4, testTime2));

        String testTime3 = "1 0724 1701";       // Monday, July 24, 5:01 PM, past closing time
        assertFalse(MarketUtils.isMarketOpenAtThisTime(m4, testTime3));

        String testTime4 = "1 0630 2359";        // Monday, June 30, 11:59 PM, day before
        assertFalse(MarketUtils.isMarketOpenAtThisTime(m4, testTime4));

        String testTime5 = "1 0701 0900";       // Monday, July 1, 9:00 AM, , at opening time
        assertTrue(MarketUtils.isMarketOpenAtThisTime(m4, testTime5));

        String testTime6 = "1 0701 0859";       // Monday, July 1, 8:59 AM, before opening time
        assertFalse(MarketUtils.isMarketOpenAtThisTime(m4, testTime6));

        String testTime7 = "4 0724 1230";       // Thursday, July 1, 12:30 PM, right date, wrong weekday
        assertFalse(MarketUtils.isMarketOpenAtThisTime(m4, testTime7));

        String testTime8 = "1 1230 1230";       // Monday, December 30, 10:30 AM, right time, wrong date
        assertFalse(MarketUtils.isMarketOpenAtThisTime(m4, testTime8));

    }

    @Test
    public void testIsMarketsOpenPastOctober() {
        // be mindful of when tests are being run!!
        boolean result1 = MarketUtils.isMarketCurrentlyOpen(m1);
        boolean result2 = MarketUtils.isMarketCurrentlyOpen(m2);
        boolean result3 = MarketUtils.isMarketCurrentlyOpen(m3);
        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
    }

    @Test
    public void testGetMarketUrl() {
        // Should not be used in productions, simply for beta, reflects the market names in the db
        final String[] marketNames = {
                "Trout Lake Farmers Market",
                "West End Farmers Market",
                "Hastings Park Winter Farmers Market",
                "Downtown Farmers Market",
                "Nat Bailey Stadium Winter Market",
                "Mount Pleasant Farmers Market",
                "UBC Farmers Market",
                "Kitsilano Farmers Market",
                "Main St Station Farmers Market"
        };

        final String[] marketUrls = {
                "https://s3-us-west-2.amazonaws.com/locally-market-images/trout_lake.jpg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/west_end.jpg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/hastings_park.jpg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/downtown.jpg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/nat_bailey.jpg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/mount_pleasant.jpeg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/ubc.jpg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/kitsilano.jpeg",
                "https://s3-us-west-2.amazonaws.com/locally-market-images/main_st.jpg"
        };

        for(int i = 0 ; i < marketNames.length; i++){
            assertEquals(marketUrls[i],MarketUtils.getMarketUrl(marketNames[i]));
        }

        assertEquals("",MarketUtils.getMarketUrl("This Market Does Not Exist"));
    }
}
