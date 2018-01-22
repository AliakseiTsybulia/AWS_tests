package com.epam.gomel.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class S3Services {
    AmazonS3 s3 = new AmazonS3Client();
    public S3Services uploadFileToS3(String bucketName, String attribute, File file) {
        try {
            System.out.println("Uploading a new object to S3 from a file");
            s3.putObject(new PutObjectRequest(bucketName, attribute, file));
            System.out.println(attribute + " file was uploaded succesfully");
        }  catch (AmazonServiceException ase) {
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        }
        return this;
    }

    public S3Services removeFileFromS3(String bucketName, String attribute) {
        try {
            System.out.println("Deleting an object");
            s3.deleteObject(bucketName, attribute);
            System.out.println(attribute + " file was deleted succesfully");
        }  catch (AmazonServiceException ase) {
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        }
        return this;
    }

    public List<String> getS3Events(String bucketName) {
        List<String> events = new ArrayList<>();
        try {
            BucketNotificationConfiguration notificationConfiguration =
                    s3.getBucketNotificationConfiguration(bucketName);
            events =
                    JsonPath.parse(notificationConfiguration.toString()).read( "$.LambdaEvent.events");
            } catch (AmazonS3Exception ase) {
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            System.out.println("Error XML" + ase.getErrorResponseXml());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        }
        return events;
    }
}
