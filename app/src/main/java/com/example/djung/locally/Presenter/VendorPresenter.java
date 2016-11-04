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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by David Jung on 03/11/16.
 */
public class VendorPresenter {
    private Context context;

    public VendorPresenter(Context context) {
        this.context = context;
    }

    public List<Vendor> fetchVendors(int marketId) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Vendor>> future = executor.submit(new FetchVendorTask(marketId));

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Fetch all the markets asynchronously from the database
     */
    class FetchVendorTask implements Callable<List<Vendor>> {

        private int marketId;

        FetchVendorTask(int marketId) {
            this.marketId = marketId;
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

            //vendorToFind.setMarketId(marketId);

            Condition idCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ)
                    .withAttributeValueList(new AttributeValue().withN(Integer.toString(marketId)));

            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(vendorToFind);

            return null;
        }
    }
}
