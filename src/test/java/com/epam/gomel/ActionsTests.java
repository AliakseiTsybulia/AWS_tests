package com.epam.gomel;

import com.epam.gomel.aws_clients.AWSConnections;
import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.S3Services;
import com.epam.gomel.test_parameters.AWSItemName;
import com.epam.gomel.test_parameters.AWSItems;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsTests {
    private AWSItemName awsItemName = new AWSItemName();
    private DynamoDBservices dynamoDBservices = new DynamoDBservices();
    private S3Services s3Services = new S3Services();
    private String s3FolderPath = new String();
    private static String fileName;

    @AfterEach
    void cleanUpServicesData() {
        s3Services.removeFileFromS3(fileName, s3FolderPath);
        dynamoDBservices.waitDynamoDBtableItemsByFileName(awsItemName.getAWSItemName(AWSItems.table_name),
                fileName, 2);
        dynamoDBservices.deleteNewDynamoDBTableItemByFileName(fileName);
    }

    @AfterAll
    static void closeAWSConnections() {
        AWSConnections.closeAWSConnections();
    }

    @DisplayName("Checking lambda was triggered after uploading file to s3")
    @ParameterizedTest
    @ValueSource(strings = { "./src/test/resources/files/testFile.txt" })
    void shouldTrigLambdaAfterUploadingFileToS3(String filePath) {
        fileName = s3Services.uploadFileToS3WithRandomName(filePath, s3FolderPath);
        assertTrue(dynamoDBservices.waitDynamoDBtableItemsByFileName(
                awsItemName.getAWSItemName(AWSItems.table_name), fileName, 1));
    }

    @DisplayName("Checking lambda was triggered after removing uploaded to s3 file")
    @ParameterizedTest
    @ValueSource(strings = { "./src/test/resources/files/testFile.txt" })
    void shouldTrigLambdaAfterRemovingUploadedFileToS3(String filePath) {
        fileName = s3Services.uploadFileToS3WithRandomName(filePath, s3FolderPath);
        s3Services.removeFileFromS3(fileName, s3FolderPath);
        assertTrue(dynamoDBservices.waitDynamoDBtableItemsByFileName(
                awsItemName.getAWSItemName(AWSItems.table_name), fileName, 2));
    }
}
