package com.example.djung.locally;

import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Utils.DateUtils;
import com.example.djung.locally.Utils.MarketUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Angy Chung on 2016-11-09.
 */

public class DateUtilsUnitTest {

    @Test
    public void testParseHours(){
        String result = DateUtils.parseHours("00:00-00:00,08:30-15:00,00:00-00:00,00:00-00:00," +
                "00:00-00:00,00:00-00:00,06:00-12:30");
        assertEquals("Tuesdays 8:30 AM - 3:00 PM\nSundays 6:00 AM - 12:30 PM\n", result);
    }

    @Test
    public void testAlwaysClosedOrOpenParseHours() {
        String result1 = DateUtils.parseHours("00:00-00:00,00:00-00:00,00:00-00:00,00:00-00:00," +
                "00:00-00:00,00:00-00:00,00:00-00:00");     // always closed
        assertEquals("", result1);

        String result2 = DateUtils.parseHours("00:00-23:59,00:00-23:59,00:00-23:59,00:00-23:59," +
                "00:00-23:59,00:00-23:59,00:00-23:59");
        assertEquals("Mondays 12:00 AM - 11:59 PM\nTuesdays 12:00 AM - 11:59 PM\nWednesdays 12:00 AM - 11:59 PM" +
                "\nThursdays 12:00 AM - 11:59 PM\nFridays 12:00 AM - 11:59 PM\nSaturdays 12:00 AM - 11:59 PM" +
                "\nSundays 12:00 AM - 11:59 PM\n", result2);
    }

    @Test
    public void testFormatTimeString(){
        assertEquals("12:00 AM", DateUtils.formatTimeString("00:00"));
        assertEquals("9:52 AM", DateUtils.formatTimeString("09:52"));
        assertEquals("12:00 PM", DateUtils.formatTimeString("12:00"));
        assertEquals("5:46 PM", DateUtils.formatTimeString("17:46"));
        assertEquals("11:59 PM", DateUtils.formatTimeString("23:59"));
    }

    @Test
    public void testFormatTimeRangeString() {
        assertEquals("12:00 AM - 12:00 AM", DateUtils.formatTimeRangeString("00:00-00:00"));
        assertEquals("8:30 AM - 5:00 PM", DateUtils.formatTimeRangeString("08:30-17:00"));
        assertEquals("11:48 AM - 7:31 PM", DateUtils.formatTimeRangeString("11:48-19:31"));
        assertEquals("12:00 AM - 11:59 PM", DateUtils.formatTimeRangeString("00:00-23:59"));
    }

    @Test
    public void testParseYear() {
        assertEquals("January 1 - December 31", DateUtils.parseYear("01/01-31/12"));
        assertEquals("June 10", DateUtils.parseYear("10/06-10/06"));
        assertEquals("February 14 - July 11", DateUtils.parseYear("14/02-11/07"));
    }


}
