package com.epam.gomel;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.S3Services;
import com.epam.gomel.test_parameters.AWSItems;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static com.epam.gomel.test_parameters.AWSItemName.getAWSItemName;

public class ParametersTests {

    @Test
    @DisplayName("Checking s3 bucket event notifications")
    void shouldCheckS3BucketEventNotifications() {
        assertTrue(S3Services.getInstance().getS3Events(getAWSItemName(AWSItems.bucket_name))
                .equals("[\"s3:ObjectCreated:*\",\"s3:ObjectRemoved:*\"]"));
    }

    @ParameterizedTest
    @DisplayName("Checking DynamoDB table properties")
    @ValueSource(strings = { "[{AttributeName: filePath,AttributeType: S}, " +
            "{AttributeName: fileType,AttributeType: S}, " +
            "{AttributeName: originTimeStamp,AttributeType: N}, " +
            "{AttributeName: packageId,AttributeType: S}]" })
    void shouldCheckDynamoDBTableProperties(String tableProperties) {
        assertTrue(DynamoDBservices.getInstance().CheckDynamoDBTableProperties(getAWSItemName(AWSItems.table_name),
                tableProperties));
    }

    @Test
    @DisplayName("Checking Lambda  environment variable table name")
    void shouldCheckLambdaEnvironmentVariableTableName() {
        assertFalse(getAWSItemName(AWSItems.table_name).equals(""));
    }
}
