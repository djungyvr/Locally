package com.example.djung.locally.Presenter;

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
}
