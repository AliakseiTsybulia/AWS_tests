package com.epam.gomel;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.S3Services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AWSActionsTests {

    private Properties properties = new Properties();
    private String bucketName;
    private File folder = new File("./src/test/resources/files");
    private File[] files = folder.listFiles();
    private File file = files[0];
    private DynamoDBservices dynamoDBservices = new DynamoDBservices();
    private S3Services s3Services = new S3Services();
    private int waitForUpload = 2000;
    private String tableName = "S3Content";
    private String searchAttribute = "filePath";

    @BeforeEach
    void init() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("AWS.properties");
        properties.load(is);
        bucketName = properties.getProperty("bucket_name");
    }

    @Test
    @DisplayName("Checking lambda was triggered after uploading file to s3")
    void shouldTrigLambdaAfterUploadingFileToS3() throws InterruptedException {
        s3Services.uploadFileToS3(bucketName, file.getName(), file);
        Thread.sleep(waitForUpload);
        assertEquals(dynamoDBservices.readFromDynamoDBTable(file, "actionName", tableName,
                searchAttribute),
                "ObjectCreated:Put",
                "Object was not uploaded");
        dynamoDBservices.deleteNewDynamoDBTableItemByFileName(file, tableName, searchAttribute);
        s3Services.removeFileFromS3(bucketName, file.getName());
        Thread.sleep(waitForUpload);
        dynamoDBservices.deleteNewDynamoDBTableItemByFileName(file, tableName, searchAttribute);
    }

    @Test
    @DisplayName("Checking lambda was triggered after removing uploaded to s3 file")
    void shouldTrigLambdaAfterRemovingUploadedFileToS3() throws InterruptedException {
        s3Services.uploadFileToS3(bucketName, file.getName(), file);
        Thread.sleep(waitForUpload);
        s3Services.removeFileFromS3(bucketName, file.getName());
        Thread.sleep(waitForUpload);
        assertEquals(dynamoDBservices.readFromDynamoDBTable(file, "actionName", tableName,
                searchAttribute),
                "ObjectRemoved:Delete",
                "Object was not uploaded");
        dynamoDBservices.deleteNewDynamoDBTableItemByFileName(file, tableName, searchAttribute);
        dynamoDBservices.deleteNewDynamoDBTableItemByFileName(file, tableName, searchAttribute);
    }
}
