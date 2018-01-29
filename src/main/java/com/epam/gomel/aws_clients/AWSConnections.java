package com.epam.gomel.aws_clients;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.LambdaServices;
import com.epam.gomel.services.S3Services;

public class AWSConnections {
    public static void closeAWSConnections() {
        DynamoDBservices.client.shutdown();
        LambdaServices.lambdaClient.shutdown();
        S3Services.s3.shutdown();
    }
}
