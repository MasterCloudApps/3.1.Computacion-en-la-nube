package es.codeurjc.serverless.db;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository {
    private static final String TABLE_NAME = "appusers";
    private final Table table;
    private AmazonDynamoDB dynamoDB;

    public UserRepository() {

        String dynamoDbEndpoint = System.getenv("DYNAMODB_ENDPOINT");

        if (dynamoDbEndpoint != null && !dynamoDbEndpoint.equals("")) {

            System.out.println("Local DynamoDB");
            System.out.println("DYNAMODB_ENDPOINT="+dynamoDbEndpoint);

            dynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    dynamoDbEndpoint, "us-east-1"))
                    .build();
        } else {
            System.out.println("AWS DynamoDB");
            dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        }

        table = new DynamoDB(dynamoDB).getTable(TABLE_NAME);
    }

    public ScanResult getAllUsers() {

        System.out.println(dynamoDB.listTables());
        System.out.println(table.getDescription());

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);

        return dynamoDB.scan(scanRequest);
    }

    public Item addUser(String name, String email, int age) {
        Item item = new Item()
                .withPrimaryKey("userid", UUID.randomUUID().toString())
                .withString("name", name)
                .withString("email", email)
                .withNumber("age", age);

        table.putItem(item);

        return item;
    }

    public UpdateItemOutcome updateUser(String userid, String name, String email, int age) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("userid", userid)
                .withUpdateExpression("set #na = :n, email = :e, age = :a")
                .withNameMap(new NameMap().with("#na", "name"))
                .withValueMap(new ValueMap()
                        .withString(":n", name)
                        .withString(":e", email)
                        .withNumber(":a", age))
                .withReturnValues("ALL_OLD");
        return table.updateItem(updateItemSpec);
    }

    public DeleteItemOutcome deleteUser(String userid) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("userid", userid)
                //.withConditionExpression("userid = :userid")
                //.withValueMap(new ValueMap().withString(":userid", userid))
                .withReturnValues(ReturnValue.ALL_OLD);
        return table.deleteItem(deleteItemSpec);
    }
}

