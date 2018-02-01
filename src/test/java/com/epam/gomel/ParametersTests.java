package com.epam.gomel;

import com.epam.gomel.aws_clients.AWSConnections;
import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.S3Services;
import com.epam.gomel.test_parameters.AWSItemName;
import com.epam.gomel.test_parameters.AWSItems;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class ParametersTests {
    AWSItemName awsItemName = new AWSItemName();
    S3Services s3Services = new S3Services();
    DynamoDBservices dynamoDBservices = new DynamoDBservices();

    @AfterAll
    static void closeAWSConnections() {
        AWSConnections.closeAWSConnections();
    }

    @Test
    @DisplayName("Checking s3 bucket event notifications")
    void shouldCheckS3BucketEventNotifications() {
        assertTrue(s3Services.getS3Events(awsItemName.getAWSItemName(AWSItems.bucket_name))
                .equals("[\"s3:ObjectCreated:*\",\"s3:ObjectRemoved:*\"]"));
    }

    @ParameterizedTest
    @DisplayName("Checking DynamoDB table properties")
    @ValueSource(strings = { "[{AttributeName: filePath,AttributeType: S}, " +
            "{AttributeName: fileType,AttributeType: S}, " +
            "{AttributeName: originTimeStamp,AttributeType: N}, " +
            "{AttributeName: packageId,AttributeType: S}]" })
    void shouldCheckDynamoDBTableProperties(String tableProperties) {
        assertTrue(dynamoDBservices.CheckDynamoDBTableProperties(awsItemName.getAWSItemName(AWSItems.table_name),
                tableProperties));
    }

    @Test
    @DisplayName("Checking Lambda  environment variable table name")
    void shouldCheckLambdaEnvironmentVariableTableName() {
        assertFalse(awsItemName.getAWSItemName(AWSItems.table_name).equals(""));
    }
}
