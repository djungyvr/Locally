package com.example.djung.locally.Utils;

import android.location.Location;

import java.util.Locale;

/**
 * Created by David Jung on 28/10/16.
 */
public class LocationUtils {
    /**
     * Calculates the euclidian distance of two points
     *
     * @param x1 point 1 x component
     * @param y1 point 1 y component
     * @param x2 point 2 x component
     * @param y2 point 2 y component
     * @return distance between points
     */
    public static double calculateEuclidianDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.abs((y2-y1)*(y2-y1) +(x2-x1)*(x2-x1)));
    }

    /**
     *  Returns the distance between two locations in metres
     * @param loc1
     * @param loc2
     * @return distance between in metres
     */
    public static float getDistanceBetween(Location loc1, Location loc2) {
        float[] dist = new float[1];
        Location.distanceBetween(loc1.getLatitude(), loc1.getLongitude(),
                loc2.getLatitude(), loc2.getLongitude(), dist);
        return dist[0];
    }

    /**
     *  Gets distance in metres between two locations described by their latitudes and longitudes
     * @param loc1_lat
     * @param loc1_long
     * @param loc2_lat
     * @param loc2_long
     * @return distance between in metres
     */
    public static float getDistanceBetween(double loc1_lat, double loc1_long,
                                           double loc2_lat, double loc2_long) {
        float[] dist = new float[1];
        Location.distanceBetween(loc1_lat, loc1_long, loc2_lat, loc2_long, dist);
        return dist[0];
    }

    /**
     *  Produces a string describing the distance in km rounded to 2 decimal places,
     *  given the distance in metres
     * @param distance
     * @return string representing the distance with unit km
     */
    public static String formatDistanceInKm (float distance) {
        if( (long) (distance / 1000) == distance/1000)
            return String.format(Locale.CANADA, "%d", (long) (distance/1000)) + " km";
        return String.format(Locale.CANADA, "%.2f", distance/1000) + " km";
    }
}
