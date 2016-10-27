package com.example.djung.locally.Presenter;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.example.djung.locally.Model.Constants;
import com.example.djung.locally.Model.Market;
import com.example.djung.locally.Model.Vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by djung on 26/10/16.
 */
public class MarketPresenter {
    private Context context;

    public MarketPresenter(Context context) {
        this.context = context;
    }

    public List<Market> fetchMarkets() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Market>> future = executor.submit(new FetchMarketsTask());

        executor.shutdown(); // Important!

        return future.get();
    }

    /**
     * Fetch all the markets asynchronously from the database
     */
    class FetchMarketsTask implements Callable<List<Market>> {
        public List<Market> call() throws Exception {
            List<Market> marketList = new ArrayList<>();

            // Initialize the Amazon Cognito credentials provider
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    Constants.AMAZON_COGNITO_IDENTITY_POOL_ID, // Identity Pool ID
                    Constants.AMAZON_DYNAMODB_REGION // Region
            );

            // Create a Dynamo Database Client
            AmazonDynamoDBClient ddbClient = Region.getRegion(Constants.AMAZON_DYNAMODB_REGION) // CRUCIAL
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
}
