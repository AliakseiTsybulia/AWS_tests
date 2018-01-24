package com.epam.gomel.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;

import java.util.stream.StreamSupport;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

public class DynamoDBservices {
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    private DynamoDB dynamo = new DynamoDB(client);
    String searchKey = "filePath";

    public Item getItemFromDynamoDBTableByFileName(String tableName, String fileName) {
        Item item = new Item();
        try {
            Table table = dynamo.getTable(tableName);
            Index index = table.getIndex(searchKey + "-index");

            QuerySpec querySpec = new QuerySpec().withExpressionSpec(
                    new ExpressionSpecBuilder().withKeyCondition(S(searchKey)
                            .eq(fileName)).buildForQuery());

            ItemCollection<QueryOutcome> ic = index.query(querySpec);
            item = StreamSupport.stream(ic.spliterator(), false).max(
                    (v1, v2) -> (int)(v1.getLong("originTimeStamp")
                            - v2.getLong("originTimeStamp"))).orElse(null);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return item;
    }

    public String readFromDynamoDBTableByFileName(String tableName, String fileName, String outputAttribute){
        Item item = getItemFromDynamoDBTableByFileName(tableName, fileName);
        return item != null ? item.getString(outputAttribute) : null;
    }

    public DynamoDBservices deleteNewDynamoDBTableItemByFileName(String tableName, String fileName) {
        Table table = dynamo.getTable(tableName);
        Item item = getItemFromDynamoDBTableByFileName(tableName, fileName);
        try {
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

    public boolean CheckDynamoDBTableProperties(String tableName, String expectedProperties) {
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
