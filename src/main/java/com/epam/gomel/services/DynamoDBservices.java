package com.epam.gomel.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;


import java.io.File;

import java.util.stream.StreamSupport;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

public class DynamoDBservices {
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    private DynamoDB dynamo = new DynamoDB(client);

    public String readFromDynamoDBTable(File searchAttributeValue, String outputAttribute, String tableName,
                                        String searchAttribute){
        Item item = new Item();
        try {
            Table table = dynamo.getTable(tableName);
            Index index = table.getIndex(searchAttribute + "-index");

            QuerySpec querySpec = new QuerySpec().withExpressionSpec(
                    new ExpressionSpecBuilder().withKeyCondition(S(searchAttribute)
                            .eq(searchAttributeValue.getName())).buildForQuery());

            ItemCollection<QueryOutcome> ic = index.query(querySpec);
            item = StreamSupport.stream(ic.spliterator(), false).max(
                    (v1, v2) -> (int)(v1.getLong("originTimeStamp")
                            - v2.getLong("originTimeStamp"))).orElse(null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return item.getString(outputAttribute);
    }

    public DynamoDBservices deleteNewDynamoDBTableItemByFileName(File searchAttributeValue, String tableName,
                                                                 String searchAttribute) {
        try {
            Table table = dynamo.getTable(tableName);
            Index index = table.getIndex(searchAttribute + "-index");

            QuerySpec querySpec = new QuerySpec().withExpressionSpec(
                    new ExpressionSpecBuilder().withKeyCondition(S(searchAttribute)
                            .eq(searchAttributeValue.getName())).buildForQuery());

            ItemCollection<QueryOutcome> ic = index.query(querySpec);
            Item item = StreamSupport.stream(ic.spliterator(), false).max(
                    (v1, v2) -> (int)(v1.getLong("originTimeStamp")
                            - v2.getLong("originTimeStamp"))).orElse(null);

            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey(
                            "packageId", item.getString("packageId"),
                            "originTimeStamp", Long.valueOf(item.getJSON("originTimeStamp"))));
            table.deleteItem(deleteItemSpec);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return this;
    }

    public boolean CheckDynamoDBTableProperties(String expectedProperties) {
        String actualProperties = new String();
        try {
            Table table = dynamo.getTable("S3Content");
            actualProperties = table.describe().getAttributeDefinitions().toString();
            } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return actualProperties.equals(expectedProperties);
    }
}
