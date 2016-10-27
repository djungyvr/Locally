package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

/**
 * Describes a Vendor item
 *
 * This class should only be able to GET from the database and not be able to make changes.
 * The produce is fixed and the vendors will have a choice of selecting from out database.
 * The developers will change the database entries.
 *
 * Created by David Jung on 15/10/16.
 */

@DynamoDBTable(tableName = "Product")
public class Product {
    // Primary Key
    private int id;

    // Name of the item
    private String name;

    @DynamoDBHashKey(attributeName="Product.Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
