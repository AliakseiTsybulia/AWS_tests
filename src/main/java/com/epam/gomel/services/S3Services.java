package com.epam.gomel.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.epam.gomel.test_parameters.AWSItemName;
import com.epam.gomel.test_parameters.AWSItems;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Random;

public class S3Services {
    public static AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    private TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
    private String bucketName = new AWSItemName().getAWSItemName(AWSItems.bucket_name);

    public String uploadFileToS3WithOriginalName(String filePath, String s3folder) {
        Upload upload;
        File file = new File(filePath);
            upload = tm.upload(bucketName, s3folder + file.getName(), file);
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(upload.getDescription() + ": Success");
        return file.getName();
    }

    public String uploadFileToS3WithRandomName(String filePath, String s3folder) {
            Upload upload;
            File file = new File(filePath);
            String fileExtension = FilenameUtils.getExtension(file.getName());
            String fileName = String.valueOf(new Random().nextInt()) + "." + fileExtension;
                upload = tm.upload(bucketName, s3folder + fileName, file);
            try {
                upload.waitForCompletion();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(upload.getDescription() + ": Success");
        return fileName;
    }

    public void removeFileFromS3(String key, String s3Folder) {
        if (s3.doesObjectExist(bucketName, s3Folder + key)) {
            System.out.println("Deleting an object");
            s3.deleteObject(bucketName, s3Folder + key);
            System.out.println(key + " file was deleted succesfully");
        } else
            System.out.println(key + " does not exist or was permanently removed");
    }

    public String getS3Events(String bucketName) {
        BucketNotificationConfiguration notificationConfiguration = s3.getBucketNotificationConfiguration(bucketName);
        String events = JsonPath.parse(notificationConfiguration.toString()).read("$.LambdaEvent.events")
                .toString();
        return events;
    }
}
