package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

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
public class Market {
    // Primary Key
    private int id;

    // Name of the market
    private String name;

    // Location of market
    private double latitude;

    private double longitude;

    @DynamoDBHashKey(attributeName="Market.Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName="Market.Name")
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
}
