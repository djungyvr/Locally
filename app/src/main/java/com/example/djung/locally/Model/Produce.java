package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

/**
 * Describes a Produce item
 *
 * This class should only be able to GET from the database and not be able to make changes.
 * The produce is fixed and the vendors will have a choice of selecting from out database.
 * The developers will change the database entries.
 *
 * Created by David Jung on 15/10/16.
 */

@DynamoDBTable(tableName = "Produce")
public class Produce {
    // Primary Key
    private int id;
    // Name of the produce
    private String name;

    @DynamoDBHashKey(attributeName="Produce.Id")
    public long getId() {
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
