package com.epam.gomel.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import org.awaitility.core.ConditionTimeoutException;

import java.util.stream.StreamSupport;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;
import static org.awaitility.Awaitility.await;

public class DynamoDBservices {
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    private DynamoDB dynamo = new DynamoDB(client);
    String searchKey = "filePath";

    public ItemCollection<QueryOutcome> getItemFromDynamoDBTableByFileName(String tableName, String fileName) {
        ItemCollection<QueryOutcome> items = null;
        try {
            Table table = dynamo.getTable(tableName);
            Index index = table.getIndex(searchKey + "-index");
            QuerySpec querySpec = new QuerySpec().withExpressionSpec(
                    new ExpressionSpecBuilder().withKeyCondition(S(searchKey)
                            .eq(fileName)).buildForQuery());
            items = index.query(querySpec);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return items;
    }

    public boolean deleteNewDynamoDBTableItemByFileName(String tableName, String fileName) {
        boolean result = true;
        Table table = dynamo.getTable(tableName);
        ItemCollection<QueryOutcome> items = getItemFromDynamoDBTableByFileName(tableName, fileName);
        if (items != null) {
            StreamSupport.stream(items.spliterator(), false).forEach(item -> {
                DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey(
                            "packageId", item.getString("packageId"),
                            "originTimeStamp", Long.valueOf(item.getJSON("originTimeStamp"))));
                table.deleteItem(deleteItemSpec);
            });
        } else {
            result = false;
            System.err.println("Item with file name '" + fileName + "' is not exist");
        }
        return result;
    }

    public boolean CheckDynamoDBTableProperties(String tableName, String expectedProperties) {
        String actualProperties = new String();
        try {
            Table table = dynamo.getTable(tableName);
            actualProperties = table.describe().getAttributeDefinitions().toString();
            } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return actualProperties.equals(expectedProperties);
    }

    public boolean waitDynamoDBtableItemsByFileName(String tableName, String fileName, int itemCount) {
        boolean result = true;
        try {
            await().until(() -> StreamSupport.stream(getItemFromDynamoDBTableByFileName(tableName, fileName)
                    .spliterator(), false).count() == itemCount);
        } catch (ConditionTimeoutException e) {
            result = false;
            System.out.println(e.getMessage());
        }
        return result;
    }
}
