package es.codeurjc.serverless.db;

import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class UserRepository {
    private static final String TABLE_NAME = "appusers";
    private final Table table;

    public UserRepository() {
        DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
        table = dynamoDB.getTable(TABLE_NAME);
    }

    public ScanResult getAllUsers() {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);
        return AmazonDynamoDBClientBuilder.defaultClient().scan(scanRequest);
    }

    public PutItemOutcome addUser(String name, String email, int age) {
        Item item = new Item()
                .withPrimaryKey("userid", UUID.randomUUID().toString())
                .withString("name", name)
                .withString("email", email)
                .withNumber("age", age);
        return table.putItem(item);
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

