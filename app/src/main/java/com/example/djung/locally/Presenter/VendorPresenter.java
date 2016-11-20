package com.example.djung.locally.Presenter;

import android.content.Context;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.example.djung.locally.AWS.AwsConfiguration;
import com.example.djung.locally.Model.Vendor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Handles vendor database stuff
 *
 * Created by David Jung on 03/11/16.
 */
public class VendorPresenter {
    private Context context;

    public VendorPresenter(Context context) {
        this.context = context;
    }

    /**
     * Fetch all the vendors asynchronously from the database that is associated with a market name
     *
     * Returns an empty list if not found
     */
    public List<Vendor> fetchVendors(String marketName) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Vendor>> future = executor.submit(new FetchVendorsTask(marketName));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Updates the description of the vendor
     */
    public boolean updateVendorDetails(String marketName, String vendorName, String description) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new UpdateVendorDetails(marketName,vendorName, description));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Updates the items of the vendor
     *
     * Returns true if succesfully updated, false otherwise
     */
    public boolean updateVendorProducts(String marketName, String vendorName, Set<String> vendorItems,String description) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new UpdateVendorProducts(marketName,vendorName, vendorItems,description));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Fetch all the products asynchronously from the database that is associated with a vendor
     *
     * Returns an empty list if not found
     */
    public Set<String> fetchVendorProducts(String marketName, String vendorName) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Set<String>> future = executor.submit(new FetchVendorProducts(marketName,vendorName));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Fetch vendor asynchronously from the database with primary key marketName and range key vendor name
     *
     * Returns a vendor or null
     */
    public Vendor fetchVendor(String marketName, String vendorName) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Vendor>> future = executor.submit(new FetchVendorTask(marketName,vendorName));

        executor.shutdown(); // Important!

        List<Vendor> vendors = future.get();

        if(vendors.isEmpty())
            return null;
        else
            return vendors.get(0);
    }

    /**
     * Add a vendor asynchronously asynchronously from the database with primary key marketName and range key vendor name
     *
     * Returns the Vendor that was added
     */
    public Vendor addVendor(String marketName, String vendorName, String vendorEmail, String vendorPhoneNumber) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Vendor> future = executor.submit(new AddVendor(marketName,vendorName,vendorPhoneNumber,vendorEmail));

        executor.shutdown(); // Important!

        Vendor addedVendor = future.get();

        return addedVendor;
    }

    /**
     * In a given market return the vendors that have atleast one of your grocery list items
     *
     * Returns the Vendor that was added
     */
    public List<Vendor> lookForVendors(String marketName, List<String> groceryList) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Vendor>> future = executor.submit(new LookForVendorsTask(marketName,groceryList));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Return the vendors that have the given item
     *
     * Returns the Vendors that have the item
     */
    public List<Vendor> lookForVendorsItem(String item) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Vendor>> future = executor.submit(new LookForVendorsItemTask(item));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * In a given market return the items that have been found in your grocery list items
     *
     * Returns a list of grocery list items that were found
     */
    public List<String> lookupGroceryList(String marketName, List<String> groceryList) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<String>> future = executor.submit(new LookForAvailableTask(marketName,groceryList));

        executor.shutdown(); // Important!

        return future.get();
    }

    private class FetchVendorTask implements Callable<List<Vendor>> {

        private String marketName;
        private String vendorName;

        FetchVendorTask(String marketName, String vendorName) {
            this.marketName = marketName;
            this.vendorName = vendorName;
        }

        @Override
        public List<Vendor> call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Vendor vendorToFind = new Vendor();
            // Set primary hash key
            vendorToFind.setMarketName(marketName);


            // Note ComparisonOperator.CONTAINS is not supported by query only by scan
            // Set range key condition
            Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
                    .withAttributeValueList(new AttributeValue().withS(vendorName.toString()));

            DynamoDBQueryExpression query = new DynamoDBQueryExpression()
                    .withHashKeyValues(vendorToFind)
                    .withRangeKeyCondition("Vendor.Name",rangeKeyCondition)
                    .withConsistentRead(false);

            List<Vendor> vendors = mapper.query(Vendor.class,query);

            return vendors;
        }
    }

    /**
     * Updates the vendor product list
     */
    private class UpdateVendorProducts implements Callable<Boolean> {
        private final String TAG = "UpdateProducts";
        private String marketName;
        private String vendorName;
        private Set<String> productCodes;
        private String description;

        UpdateVendorProducts(String marketName, String vendorName, Set<String> productCodes, String description) {
            this.marketName = marketName;
            this.vendorName = vendorName;
            this.productCodes = productCodes;
            this.description = description;
        }

        @Override
        public Boolean call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Vendor vendorToFind = new Vendor();
            // Set primary hash key
            vendorToFind.setMarketName(marketName);


            // Note ComparisonOperator.CONTAINS is not supported by query only by scan
            // Set range key condition
            Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
                    .withAttributeValueList(new AttributeValue().withS(vendorName.toString()));

            DynamoDBQueryExpression query = new DynamoDBQueryExpression()
                    .withHashKeyValues(vendorToFind)
                    .withRangeKeyCondition("Vendor.Name",rangeKeyCondition)
                    .withConsistentRead(false);


            List<Vendor> vendors = mapper.query(Vendor.class,query);

            if(!vendors.isEmpty()) {
                vendorToFind = vendors.get(0);
                if(productCodes.isEmpty())
                    productCodes.add("PLACEHOLDER");
                if(description.isEmpty() || description == null)
                    description = "Description";
                vendorToFind.setItemSet(productCodes);
                vendorToFind.setDescription(description);
                mapper.save(vendorToFind);
                Log.e(TAG,"Succesfully updated vendor items");
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Updates the vendor product list
     */
    private class UpdateVendorDetails implements Callable<Boolean> {
        private final String TAG = "UpdateDetail";
        private String marketName;
        private String vendorName;
        private String description;

        UpdateVendorDetails(String marketName, String vendorName, String description) {
            this.marketName = marketName;
            this.vendorName = vendorName;
            this.description = description;
        }

        @Override
        public Boolean call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Vendor vendorToFind = new Vendor();
            // Set primary hash key
            vendorToFind.setMarketName(marketName);


            // Note ComparisonOperator.CONTAINS is not supported by query only by scan
            // Set range key condition
            Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
                    .withAttributeValueList(new AttributeValue().withS(vendorName.toString()));

            DynamoDBQueryExpression query = new DynamoDBQueryExpression()
                    .withHashKeyValues(vendorToFind)
                    .withRangeKeyCondition("Vendor.Name",rangeKeyCondition)
                    .withConsistentRead(false);


            List<Vendor> vendors = mapper.query(Vendor.class,query);

            if(!vendors.isEmpty()) {
                vendorToFind = vendors.get(0);
                if(description.isEmpty() || description == null)
                    description = "Add your own description!";
                vendorToFind.setDescription(description);
                mapper.save(vendorToFind);
                Log.e(TAG,"Succesfully updated vendor description");
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Fetch the vendor product list
     */
    private class FetchVendorProducts implements Callable<Set<String>> {

        private String marketName;
        private String vendorName;

        FetchVendorProducts(String marketName, String vendorName) {
            this.marketName = marketName;
            this.vendorName = vendorName;
        }

        @Override
        public Set<String> call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Vendor vendorToFind = new Vendor();
            // Set primary hash key
            vendorToFind.setMarketName(marketName);


            // Note ComparisonOperator.CONTAINS is not supported by query only by scan
            // Set range key condition
            Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
                    .withAttributeValueList(new AttributeValue().withS(vendorName.toString()));

            DynamoDBQueryExpression query = new DynamoDBQueryExpression()
                    .withHashKeyValues(vendorToFind)
                    .withRangeKeyCondition("Vendor.Name",rangeKeyCondition)
                    .withConsistentRead(false);

            List<Vendor> vendor = mapper.query(Vendor.class,query);

            if(!vendor.isEmpty()) {
                return vendor.get(0).getItemSet();
            }

            return new HashSet<>();
        }
    }

    /**
     * Add a vendor to the database
     */
    private class AddVendor implements Callable<Vendor> {

        private String marketName;
        private String vendorName;
        private String vendorPhoneNumber;
        private String vendorEmail;

        AddVendor(String marketName, String vendorName, String vendorPhoneNumber, String vendorEmail) {
            this.marketName = marketName;
            this.vendorName = vendorName;
            this.vendorEmail = vendorEmail;
            this.vendorPhoneNumber = vendorPhoneNumber;
        }

        @Override
        public Vendor call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Vendor vendorToAdd = new Vendor();

            vendorToAdd.setMarketName(marketName);
            vendorToAdd.setName(vendorName);

            HashSet<String> initialHashSet = new HashSet<>();
            initialHashSet.add("PLACEHOLDER");
            vendorToAdd.setItemSet(initialHashSet);

            vendorToAdd.setDescription("Change this to your own description!");

            vendorToAdd.setEmail(this.vendorEmail);
            vendorToAdd.setPhoneNumber(this.vendorPhoneNumber);

            mapper.save(vendorToAdd);
            return vendorToAdd;
        }
    }

    /**
     * Fetch vendors in a given market that have one or more items in your grocery list
     */
    private class LookForVendorsTask implements Callable<List<Vendor>> {

        private String marketName;
        private List<String> items;

        LookForVendorsTask(String marketName, List<String> items) {
            this.marketName = marketName;
            this.items = items;
        }

        @Override
        public List<Vendor> call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBQueryExpression<Vendor> query =
                    new DynamoDBQueryExpression<>();
            Vendor hashKeyValues = new Vendor();
            hashKeyValues.setMarketName(marketName);
            query.setHashKeyValues(hashKeyValues);

            List<Vendor> matchingVendors = new ArrayList<>();

            List<Vendor> vendors = mapper.query(Vendor.class, query);
            // Iterate through the vendors and add to the list if they have it
            for (Vendor vendor : vendors) {
                for (String item : items) {
                    // Add if it contains the grocery list item and if the vendor isn't already added
                    if (vendor.getItemSet().contains(item) && !matchingVendors.contains(vendor)) {
                        matchingVendors.add(vendor);
                    }
                }
            }

            return matchingVendors;
        }
    }

    /**
     * Fetch the list of items that are available from the grocery list in a given market
     */
    private class LookForAvailableTask implements Callable<List<String>> {

        private String marketName;
        private List<String> items;

        LookForAvailableTask(String marketName, List<String> items) {
            this.marketName = marketName;
            this.items = items;
        }

        @Override
        public List<String> call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBQueryExpression<Vendor> query =
                    new DynamoDBQueryExpression<>();
            Vendor hashKeyValues = new Vendor();
            hashKeyValues.setMarketName(marketName);
            query.setHashKeyValues(hashKeyValues);

            List<String> matchingItems = new ArrayList<>();

            List<Vendor> vendors = mapper.query(Vendor.class, query);
            // Iterate through the vendors and add item to the list if they have it
            for (Vendor vendor : vendors) {
                for (String item : items) {
                    // Add if it contains the grocery list item and if the vendor isn't already added
                    if (vendor.getItemSet().contains(item) && !matchingItems.contains(item)) {
                        matchingItems.add(item);
                    }
                }
            }

            return matchingItems;
        }
    }

    /**
     * Fetch all the vendors that are associated with that market name
     */
    private class FetchVendorsTask implements Callable<List<Vendor>> {

        private String marketName;

        FetchVendorsTask(String marketName) {
            this.marketName = marketName;
        }

        @Override
        public List<Vendor> call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBQueryExpression<Vendor> query =
                    new DynamoDBQueryExpression<>();
            Vendor hashKeyValues = new Vendor();
            hashKeyValues.setMarketName(marketName);
            query.setHashKeyValues(hashKeyValues);

            return mapper.query(Vendor.class,query);
        }
    }

    /**
     * Fetch vendors that have the given item in stock
     */
    private class LookForVendorsItemTask implements Callable<List<Vendor>> {

        private String item;

        LookForVendorsItemTask(String item) {
            this.item = item;
        }

        @Override
        public List<Vendor> call() throws Exception {
            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    AwsConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    AwsConfiguration.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(AwsConfiguration.AMAZON_DYNAMODB_REGION) // CRUCIAL
                    .createClient(
                            AmazonDynamoDBClient.class,
                            credentialsProvider,
                            new ClientConfiguration()
                    );

            // Create a mapper from the client
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

            // Get the list of vendors
            List<Vendor> vendorList = mapper.scan(Vendor.class, scanExpression);

            List<Vendor> matchingVendors = new ArrayList<>();

            // Iterate through the vendors and add to the list if they have it
            for (Vendor vendor : vendorList) {
                // Add if it contains the item and if the vendor isn't already added
                if (vendor.getItemSet().contains(item) && !matchingVendors.contains(vendor)) {
                    matchingVendors.add(vendor);
                }
            }
        return matchingVendors;
    }
}
}
