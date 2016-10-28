package com.example.djung.locally.Presenter;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.*;
import com.example.djung.locally.AWS.AwsConfiguration;
import com.example.djung.locally.Model.Market;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Initial implementation for fetching and writing from the market database to hand over to the
 * views. All tasks run asynchronously.
 *
 * Created by David Jung on 26/10/16.
 */
public class MarketPresenter {
    private Context context;

    public MarketPresenter(Context context) {
        this.context = context;
    }

    public List<Market> fetchMarkets() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Market>> future = executor.submit(new FetchAllMarketsTask());

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Fetch all the markets asynchronously from the database
     */
    class FetchAllMarketsTask implements Callable<List<Market>> {
        @Override
        public List<Market> call() throws Exception {
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

            return mapper.scan(Market.class, scanExpression);
        }
    }

    /**
     * Fetch all the open markets asynchronously from the database
     */
    class FetchAllOpenMarketsTask implements Callable<List<Market>> {
        @Override
        public List<Market> call() throws Exception {
            throw new UnsupportedOperationException("Fetch all open markets task not implemented");
        }
    }
}
