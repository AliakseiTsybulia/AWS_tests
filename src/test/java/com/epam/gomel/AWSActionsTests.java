package com.epam.gomel;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.LambdaServices;
import com.epam.gomel.services.S3Services;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AWSActionsTests {
    private Properties properties = new Properties();
    private String bucketName;
    private DynamoDBservices dynamoDBservices = new DynamoDBservices();
    private S3Services s3Services = new S3Services();
    private LambdaServices lambdaServices = new LambdaServices();
    private String tableName;
    private String functionName;
    private String s3FolderPath;
    private static String fileName;

    @BeforeEach
    void initAWSActionsTests() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("AWS.properties");
        properties.load(is);
        bucketName = properties.getProperty("bucket_name");
        functionName = properties.getProperty("lambda_function_name");
        tableName = lambdaServices.getLambdaConfigVariables(functionName).get("InventoryLambda_DYNAMODB_NAME");
        s3FolderPath = "";
    }

    @AfterEach
    void cleanUpServicesData() {
        s3Services.removeFileFromS3(bucketName, fileName, s3FolderPath);
        dynamoDBservices.waitDynamoDBtableItemsByFileName(tableName, fileName, 2);
        dynamoDBservices.deleteNewDynamoDBTableItemByFileName(tableName, fileName);
    }

    @DisplayName("Checking lambda was triggered after uploading file to s3")
    @ParameterizedTest
    @ValueSource(strings = { "./src/test/resources/files/testFile.txt" })
    void shouldTrigLambdaAfterUploadingFileToS3(String filePath) throws InterruptedException {
        fileName = s3Services.uploadFileToS3(bucketName, filePath, s3FolderPath);
        assertTrue(dynamoDBservices.waitDynamoDBtableItemsByFileName(tableName, fileName, 1));
    }

    @DisplayName("Checking lambda was triggered after removing uploaded to s3 file")
    @ParameterizedTest
    @ValueSource(strings = { "./src/test/resources/files/testFile.txt" })
    void shouldTrigLambdaAfterRemovingUploadedFileToS3(String filePath) throws InterruptedException {
        fileName = s3Services.uploadFileToS3(bucketName, filePath, s3FolderPath);
        s3Services.removeFileFromS3(bucketName, fileName, s3FolderPath);
        assertTrue(dynamoDBservices.waitDynamoDBtableItemsByFileName(tableName, fileName, 2));
    }
}
