package com.example.djung.locally.Model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

import java.util.Set;

/**
 * Describes a Vendor
 * Created by David Jung on 07/10/16.
 */

/**
 * Example Use:
 // Remember to add the AwsConfiguration.java file inside the Model package
 // Remember to add the following import statements
 import com.amazonaws.auth.CognitoCachingCredentialsProvider;
 import com.amazonaws.regions.Regions;
 import com.amazonaws.services.dynamodbv2.*;

 // Initialize the Amazon Cognito credentials provider
 CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
 getApplicationContext(),
 AwsConfiguration.IDENTITY_POOL_ID, // Identity Pool ID
 Regions.US_WEST_2 // Region
 );

 // Create a Dynamo Database Client
 AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);

 // Create a mapper from the client
 DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

 // Create a Vendor object
 Vendor vendor = new Vendor();
 vendor.setId(123);
 vendor.setName("UBC Farms");

 // Save the created vendor by using the mapper
 mapper.save(vendor);

 // To retrieve the created Vendor use
 Vendor selectedVendor = mapper.load(Vendor.id, 123);

 // To edit just set the desired attribute(s)
 selectedVendor.setName("Kitsilano Tomatoes");

 // Save the changed object
 mapper.save(selectedVendor);
 */

@DynamoDBTable(tableName = "Vendor")
public class Vendor {
    // Name of the market it belongs to
    private String marketName;
    // Name of the vendor
    private String name;
    // Description of the vendor
    private String description;
    // Link to vendor image
    private String imageResource;
    // Set of items this vendor carries
    private Set<Integer> itemIdSet;

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

    @DynamoDBAttribute(attributeName = "Vendor.ItemList")
    public Set<Integer> getItemIdSet() {
        return itemIdSet;
    }

    public void setItemIdSet(Set<Integer> itemIdSet) {
        this.itemIdSet = itemIdSet;
    }
}
