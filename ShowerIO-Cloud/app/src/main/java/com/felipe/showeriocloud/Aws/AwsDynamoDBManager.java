package com.felipe.showeriocloud.Aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

//This class is designed to initialize and deal with DynamoDB connections after the user is authenticated
public class AwsDynamoDBManager {

    // Declare a DynamoDBMapper object
    public static DynamoDBMapper dynamoDBMapper;


    public void initializeDynamoDb() {
            //final AWSCredentialsProvider awsCredentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
            //final AWSConfiguration awsConfiguration = AWSMobileClient.getInstance().getConfiguration();

        final CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider = AuthorizationHandle.cognitoCachingCredentialsProvider;

        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(cognitoCachingCredentialsProvider);


         this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .build();
    }

}
