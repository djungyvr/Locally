package com.example.djung.locally;

import com.example.djung.locally.Model.Market;

/**
 * Created by Angy Chung on 2016-11-09.
 *
 * This class is meant to help test methods using the Market class
 * without having to interface with the database
 */

public class MarketTest extends Market {

    // Primary Key
    private int id;
    // Name of the market
    private String name;
    // Latitude of market
    private double latitude;
    // Longitude of market
    private double longitude;
    // Address of market
    private String address;
    // Description
    private String description;
    // Set of days and hours this market is open
    private String dailyHours;

    // Time of year open
    private String yearOpen;

    @Override
    public int getId() {
        return id;
    }
    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String getAddress() {
        return address;
    }
    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDailyHours() {
        return dailyHours;
    }
    @Override
    public void setDailyHours(String dailyHours) {
        this.dailyHours = dailyHours;
    }

    @Override
    public String getYearOpen() {
        return yearOpen;
    }
    @Override
    public void setYearOpen(String yearOpen) {
        this.yearOpen = yearOpen;
    }
}

