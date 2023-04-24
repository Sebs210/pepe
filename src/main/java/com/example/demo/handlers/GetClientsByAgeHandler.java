package com.example.demo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetClientsByAgeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        int ageFilter = Integer.parseInt(request.getQueryStringParameters().get("age"));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName("Cliente")
                .withFilterExpression("Edad >= :age")
                .withExpressionAttributeValues(Collections.singletonMap(":age", new AttributeValue().withN(String.valueOf(ageFilter))));

        ScanResult scanResult = dynamoDb.scan(scanRequest);

        List<Map<String, AttributeValue>> clientsByAge = scanResult.getItems().stream()
                .collect(Collectors.toList());

        List<Map<String, String>> clientsByAgeJson = clientsByAge.stream()
                .map(client -> client.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().getS()
                        ))
                )
                .collect(Collectors.toList());

        String responseBody = new Gson().toJson(clientsByAgeJson);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(responseBody);
        return response;
    }
}
