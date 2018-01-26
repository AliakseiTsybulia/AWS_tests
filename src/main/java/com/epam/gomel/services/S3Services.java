package com.epam.gomel.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Random;

public class S3Services {
    private static S3Services instance = new S3Services();
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();

    public static S3Services getInstance() {
        if (instance == null) {
            instance = new S3Services();
        }
        return instance;
    }

    public String uploadFileToS3(String bucketName, String filePath, String s3folder, boolean leaveOriginalFileName) {
            Upload upload;
            File file = new File(filePath);
            String fileExtension = FilenameUtils.getExtension(file.getName());
            String fileName = String.valueOf(new Random().nextInt()) + "." + fileExtension;
            if (leaveOriginalFileName)
                upload = tm.upload(bucketName, s3folder + file.getName(), file);
            else
                upload = tm.upload(bucketName, s3folder + fileName, file);
            try {
                upload.waitForCompletion();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(upload.getDescription() + ": Success");
        return fileName;
    }

    public void removeFileFromS3(String bucketName, String key, String s3Folder) {
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
