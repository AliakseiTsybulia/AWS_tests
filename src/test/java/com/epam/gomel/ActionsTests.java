package com.epam.gomel;

import com.epam.gomel.services.DynamoDBservices;
import com.epam.gomel.services.S3Services;
import com.epam.gomel.test_parameters.AWSItems;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static com.epam.gomel.test_parameters.AWSItemName.getAWSItemName;

public class ActionsTests {
    private String s3FolderPath = new String();
    private static String fileName;

    @AfterEach
    void cleanUpServicesData() {
        S3Services.getInstance().removeFileFromS3(getAWSItemName(AWSItems.bucket_name), fileName, s3FolderPath);
        DynamoDBservices.getInstance().waitDynamoDBtableItemsByFileName(getAWSItemName(AWSItems.table_name),
                fileName, 2);
        DynamoDBservices.getInstance().deleteNewDynamoDBTableItemByFileName(getAWSItemName(AWSItems.table_name),
                fileName);
    }

    @DisplayName("Checking lambda was triggered after uploading file to s3")
    @ParameterizedTest
    @ValueSource(strings = { "./src/test/resources/files/testFile.txt" })
    void shouldTrigLambdaAfterUploadingFileToS3(String filePath) {
        fileName = S3Services.getInstance().uploadFileToS3(getAWSItemName(AWSItems.bucket_name),
                filePath, s3FolderPath, false);
        assertTrue(DynamoDBservices.getInstance().waitDynamoDBtableItemsByFileName(
                getAWSItemName(AWSItems.table_name), fileName, 1));
    }

    @DisplayName("Checking lambda was triggered after removing uploaded to s3 file")
    @ParameterizedTest
    @ValueSource(strings = { "./src/test/resources/files/testFile.txt" })
    void shouldTrigLambdaAfterRemovingUploadedFileToS3(String filePath) {
        fileName = S3Services.getInstance().uploadFileToS3(getAWSItemName(AWSItems.bucket_name),
                filePath, s3FolderPath, false);
        S3Services.getInstance().removeFileFromS3(getAWSItemName(AWSItems.bucket_name), fileName, s3FolderPath);
        assertTrue(DynamoDBservices.getInstance().waitDynamoDBtableItemsByFileName(
                getAWSItemName(AWSItems.table_name), fileName, 2));
    }
}
