package com.epam.gomel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.S3Services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AWSParameters {
    private Properties properties = new Properties();
    private String bucketName;
    private List<String> eventActions = Arrays.asList("s3:ObjectCreated:*", "s3:ObjectRemoved:*");
    private S3Services s3Services = new S3Services();
    private DynamoDBservices dynamoDBservices = new DynamoDBservices();
    private String tableProperties = "[{AttributeName: filePath,AttributeType: S}, " +
            "{AttributeName: fileType,AttributeType: S}, " +
            "{AttributeName: originTimeStamp,AttributeType: N}, " +
            "{AttributeName: packageId,AttributeType: S}]";

    @BeforeEach
    void init() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("AWS.properties");
        properties.load(is);
        bucketName = properties.getProperty("bucket_name");
    }

    @Test
    @DisplayName("Checking s3 bucket event notifications")
    void shouldCheckS3BucketEventNotifications() {
        assertTrue(s3Services.getS3Events(bucketName).containsAll(eventActions));
    }

    @Test
    @DisplayName("Checking DynamoDB table properties")
    void shouldCheckDynamoDBTableProperties() {
        assertTrue(dynamoDBservices.CheckDynamoDBTableProperties(tableProperties));
    }
}
