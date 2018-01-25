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
    AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();
    public String uploadFileToS3(String bucketName, String filePath, String s3folder) throws InterruptedException {
            File file = new File(filePath);
            String fileExtension = FilenameUtils.getExtension(file.getName());
            String fileName = String.valueOf(new Random().nextInt()) + "." + fileExtension;
            System.out.println("Uploading a new object to S3 from a file");
            Upload upload = tm.upload(bucketName, s3folder + fileName, file);
            upload.waitForCompletion();
            System.out.println(fileName + " file was uploaded succesfully");
        return fileName;
    }

    public Boolean removeFileFromS3(String bucketName, String key, String s3Folder) {
        Boolean result = false;
            if (s3.doesObjectExist(bucketName, s3Folder + key)) {
                System.out.println("Deleting an object");
                s3.deleteObject(bucketName, s3Folder + key);
                System.out.println(key + " file was deleted succesfully");
                result = true;
            } else
                System.out.println(key + " does not exist or was permanently removed");
        return result;
    }

    public String getS3Events(String bucketName) {
        String events = new String();
            if (s3.doesBucketExistV2(bucketName)) {
                BucketNotificationConfiguration notificationConfiguration =
                        s3.getBucketNotificationConfiguration(bucketName);
                events =
                        JsonPath.parse(notificationConfiguration.toString()).read(
                                "$.LambdaEvent.events").toString();
                System.out.println(events);
            } else
                System.out.println(bucketName + " does not exist");
        return events;
    }
}
