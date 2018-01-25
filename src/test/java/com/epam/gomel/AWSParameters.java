package com.epam.gomel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.LambdaServices;
import com.epam.gomel.services.S3Services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AWSParameters {
    private Properties properties = new Properties();
    private String bucketName;
    private String functionName;
    private S3Services s3Services = new S3Services();
    private DynamoDBservices dynamoDBservices = new DynamoDBservices();
    private LambdaServices lambdaServices = new LambdaServices();
    private String tableName;

    @BeforeEach
    void initAWSParametersTests() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("AWS.properties");
        properties.load(is);
        bucketName = properties.getProperty("bucket_name");
        functionName = properties.getProperty("lambda_function_name");
        tableName = lambdaServices.getLambdaConfigVariables(functionName).get("InventoryLambda_DYNAMODB_NAME");
    }

    @ParameterizedTest
    @DisplayName("Checking s3 bucket event notifications")
    @ValueSource(strings = { "[\"s3:ObjectCreated:*\",\"s3:ObjectRemoved:*\"]" })
    void shouldCheckS3BucketEventNotifications(String events) {
        assertTrue(s3Services.getS3Events(bucketName).equals(events));
    }

    @ParameterizedTest
    @DisplayName("Checking DynamoDB table properties")
    @ValueSource(strings = { "[{AttributeName: filePath,AttributeType: S}, " +
            "{AttributeName: fileType,AttributeType: S}, " +
            "{AttributeName: originTimeStamp,AttributeType: N}, " +
            "{AttributeName: packageId,AttributeType: S}]" })
    void shouldCheckDynamoDBTableProperties(String tableProperties) {
        assertTrue(dynamoDBservices.CheckDynamoDBTableProperties(tableName, tableProperties));
    }

    @ParameterizedTest
    @DisplayName("Checking Lambda  environment variables")
    @ValueSource(strings = { "S3Content" })
    void shouldCheckLambdaEnvironmentVariableTableName(String actualTableName) {
        assertEquals(tableName, actualTableName);
    }
}
