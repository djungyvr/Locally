package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Describes a Vendor
 * Created by David Jung on 07/10/16.
 */

@DynamoDBTable(tableName = "Vendor")
public class Vendor {
    // Name of the market it belongs to
    private String marketName;
    // Name of the vendor
    private String name;
    // Description of the vendor
    private String description;
    // Set of items this vendor carries
    private Set<String> itemSet;
    // Phone number
    private String phoneNumber;
    // Email
    private String email;
    //
    private String photoUrl;

    @DynamoDBHashKey(attributeName = "Vendor.MarketName")
    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    @DynamoDBRangeKey(attributeName="Vendor.Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "Vendor.ItemSet")
    public Set<String> getItemSet() {
        // Since AWS does not allow empty string sets, we keep a place holder, but we get rid of it
        // once we try to fetch it
        return itemSet;
    }

    public void setItemSet(Set<String> itemSet) {
        this.itemSet = itemSet;
    }

    @DynamoDBAttribute(attributeName = "Vendor.Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "Vendor.PhoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @DynamoDBAttribute(attributeName = "Vendor.Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "Vendor.PhotoUrl")
    public String getPhotoUrl() {return photoUrl;}

    public void setPhotoUrl(String photoUrl) {this.photoUrl = photoUrl;}
}
