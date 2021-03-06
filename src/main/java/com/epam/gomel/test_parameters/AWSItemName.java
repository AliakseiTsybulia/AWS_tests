package com.epam.gomel.test_parameters;

import com.epam.gomel.services.LambdaServices;

import java.io.InputStream;
import java.util.Properties;

import java.io.IOException;

public class AWSItemName {
    private LambdaServices lambdaServices = new LambdaServices();
    private InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(
            "AWS.properties");
    private Properties properties = new Properties();

    public String getAWSItemName(AWSItems parameter) {
        try {
            properties.load(is);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        String propertyValue;
        switch (parameter.name()) {
            case "bucket_name":
                propertyValue = properties.getProperty("bucket_name");
                break;
            case "function_name":
                propertyValue = properties.getProperty("lambda_function_name");
                break;
            case "table_name":
                propertyValue = lambdaServices.getLambdaConfigVariables(
                        getAWSItemName(AWSItems.function_name))
                        .get("InventoryLambda_DYNAMODB_NAME");
                break;
            default:
                propertyValue = null;
                break;
        }
        return propertyValue;
    }
}
