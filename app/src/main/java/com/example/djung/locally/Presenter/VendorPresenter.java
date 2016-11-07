package com.example.djung.locally.Presenter;

import android.content.Context;

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
import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;

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
     * Updates the items of the vendor
     *
     * Returns true if succesfully updated, false otherwise
     */
    public boolean updateVendorProducts(String marketName, String vendorName, Set<Integer> productCodes) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(new UpdateVendorProducts(marketName,vendorName, productCodes));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Fetch all the product ids asynchronously from the database that is associated with a vendor
     *
     * Returns an empty list if not found
     */
    public Set<Integer> fetchVendorProducts(String marketName, String vendorName) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Set<Integer>> future = executor.submit(new FetchVendorProducts(marketName,vendorName));

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

    class FetchVendorTask implements Callable<List<Vendor>> {

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

            return mapper.query(Vendor.class,query);
        }
    }

    /**
     * Updates the vendor product list
     */
    class UpdateVendorProducts implements Callable<Boolean> {

        private String marketName;
        private String vendorName;
        private Set<Integer> productCodes;

        UpdateVendorProducts(String marketName, String vendorName, Set<Integer> productCodes) {
            this.marketName = marketName;
            this.vendorName = vendorName;
            this.productCodes = productCodes;
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
                vendorToFind.setItemIdSet(productCodes);
                mapper.save(vendorToFind);
                return true;
            } else {
                return false;
            }
        }
    }

    class FetchVendorProducts implements Callable<Set<Integer>> {

        private String marketName;
        private String vendorName;

        FetchVendorProducts(String marketName, String vendorName) {
            this.marketName = marketName;
            this.vendorName = vendorName;
        }

        @Override
        public Set<Integer> call() throws Exception {
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
                return vendor.get(0).getItemIdSet();
            }

            return new HashSet<>();
        }
    }

    /**
     * Fetch all the vendors that are associated with that market name
     */
    class FetchVendorsTask implements Callable<List<Vendor>> {

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
}
