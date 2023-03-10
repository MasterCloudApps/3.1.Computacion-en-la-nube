package es.codeurjc.serverless.db;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final UserRepository userRepository = new UserRepository();

    private final ObjectMapper mapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        switch (event.getHttpMethod()) {
            case "GET":
                return getAllUsers();
            case "POST":
                return addUser(event.getBody());
            case "PUT":
                return updateUser(event.getPathParameters().get("userid"), event.getBody());
            case "DELETE":
                return deleteUser(event.getPathParameters().get("userid"));
            default:
                return createResponse(400, "Unsupported method " + event.getHttpMethod());
        }
    }

    private APIGatewayProxyResponseEvent getAllUsers() {

        try {
            ScanResult res = userRepository.getAllUsers();

            List<Item> itemList = ItemUtils.toItemList(res.getItems());

            List<Map<String,Object>> values = new ArrayList<>();
            for(Item item: itemList){

                var value = new HashMap<String,Object>();
                value.put("userid", item.get("userid"));
                value.put("name", item.get("name"));
                value.put("email", item.get("email"));
                value.put("age", item.get("age"));
                values.add(value);
            }

            String responseBody = mapper.writeValueAsString(values);
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent addUser(String data) {

        try {

            Map<String,Object> user = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {});

            Item item = userRepository.addUser(
                    (String)user.get("name"),
                    (String)user.get("email"),
                    (Integer)user.get("age"));

            String id = (String) item.get("userid");

            String responseBody = mapper.writeValueAsString(id);

            return createResponse(201, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent updateUser(String userid, String data) {

        try {

            Map<String,Object> user = mapper.readValue(data, new TypeReference<HashMap<String, Object>>() {});

            UpdateItemOutcome res = userRepository.updateUser(
                    (String)user.get("userId"),
                    (String)user.get("name"),
                    (String)user.get("email"),
                    (Integer)user.get("age"));

            String responseBody = mapper.writeValueAsString(res.getUpdateItemResult().getAttributes());
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent deleteUser(String userid) {

        try {
            DeleteItemOutcome res = userRepository.deleteUser(userid);
            String responseBody = mapper.writeValueAsString(res.getDeleteItemResult());
            return createResponse(200, responseBody);
        } catch (Exception e) {
            System.out.println(e);
            return createResponse(500, e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String responseBody) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(responseBody);
        return response;
    }
}
