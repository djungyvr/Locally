package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.io.Serializable;

/**
 * Describes a Market
 *
 * This class should only be able to GET from the database and not be able to make changes.
 * The markets are fixed and the vendors will have a choice of being with that particular market.
 * The developers will change the database entries.
 *
 * Created by David Jung on 15/10/16.
 */

@DynamoDBTable(tableName = "Market")
public class Market  implements Serializable {
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
    // Rcf3339 opening day opening DateTime
    private String openingDayOpen;
    // Rcf3339 opening day closing DateTime
    private String openingDayClose;
    // Day of week open
    private String dayOfWeek;
    // Last day of market
    private String lastDay;

    @DynamoDBHashKey(attributeName = "Market.Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName="Market.Latitude")
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @DynamoDBAttribute(attributeName="Market.Longitude")
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @DynamoDBAttribute(attributeName="Market.Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @DynamoDBAttribute(attributeName="Market.Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName="Market.DailyHours")
    public String getDailyHours() {
        return dailyHours;
    }

    public void setDailyHours(String dailyHours) {
        this.dailyHours = dailyHours;
    }

    @DynamoDBAttribute(attributeName="Market.YearOpen")
    public String getYearOpen() {
        return yearOpen;
    }

    public void setYearOpen(String yearOpen) {
        this.yearOpen = yearOpen;
    }

    @DynamoDBAttribute(attributeName="Market.OpenRcf3339")
    public String getOpeningDayOpen() {
        return openingDayOpen;
    }

    public void setOpeningDayOpen(String openingDayOpen) {
        this.openingDayOpen = openingDayOpen;
    }

    @DynamoDBAttribute(attributeName="Market.CloseRcf3339")
    public String getOpeningDayClose() {
        return openingDayClose;
    }

    public void setOpeningDayClose(String openingDayClose) {
        this.openingDayClose = openingDayClose;
    }

    @DynamoDBAttribute(attributeName="Market.DaysOpen")
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @DynamoDBAttribute(attributeName="Market.LastDay")
    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }
}
