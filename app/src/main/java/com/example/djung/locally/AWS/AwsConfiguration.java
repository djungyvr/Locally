package com.example.djung.locally.AWS;

import com.amazonaws.regions.Regions;

/**
 * DO NOT INCLUDE THIS IN THE GITHUB REPO
 * Created by David Jung on 07/10/16.
 */
public class AwsConfiguration {
    // AWS MobileHub user agent string
    public static final String AWS_MOBILEHUB_USER_AGENT =
            "MobileHub 3f60fa07-73f4-4b28-871f-e8b9b65541d8 aws-my-sample-app-android-v0.9";

    // AMAZON COGNITO
    public static final Regions AMAZON_COGNITO_REGION =
            Regions.fromName("us-west-2");
    public static final String  AMAZON_COGNITO_IDENTITY_POOL_ID =
            "us-west-2:849a7a70-f442-4fb8-996b-17932f422ee3";
    public static final Regions AMAZON_DYNAMODB_REGION =
            Regions.fromName("us-west-2");
}
