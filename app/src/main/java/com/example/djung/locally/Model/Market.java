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
    // List of vendors
    private List<Vendor> vendorList;
    // Location of market
    //TODO:Import LatLng
    private LatLng location;

    @DynamoDBHashKey(attributeName="Market.Id")
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
