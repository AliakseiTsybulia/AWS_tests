package com.epam.gomel.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.*;
import com.amazonaws.services.lambda.model.GetFunctionRequest;

import java.util.*;

public class LambdaServices {
    private AWSLambda lambdaClient;
    private Map<String, String> configVariables;
    public Map<String, String> getLambdaConfigVariables(String functionName) {
        GetFunctionRequest functionRequest = new GetFunctionRequest().withFunctionName(functionName);
        lambdaClient = AWSLambdaClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
        configVariables = lambdaClient.getFunction(functionRequest).getConfiguration().getEnvironment().getVariables();
        return configVariables;
    }
}
