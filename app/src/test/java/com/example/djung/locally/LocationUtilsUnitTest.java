package com.example.djung.locally;

import com.example.djung.locally.Utils.LocationUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Angy Chung on 2016-11-09.
 */

public class LocationUtilsUnitTest {

    @Test
    public void testCalculateEuclidianDistance(){
        assertEquals(0.0, LocationUtils.calculateEuclidianDistance(49.2799664, -123.113858,
                49.2799664, -123.113858), 0);
        assertEquals(Math.sqrt(61), LocationUtils.calculateEuclidianDistance(1.0, -4.0,
                -4.0, 2.0), 0);
    }

    @Test
    public void testFormatDistanceInKm() {
        assertEquals("11 km", LocationUtils.formatDistanceInKm(11000.00f));
        assertEquals("7.14 km", LocationUtils.formatDistanceInKm(7142.2421f));
        assertEquals("0.93 km", LocationUtils.formatDistanceInKm(934.174678f));
    }

}
