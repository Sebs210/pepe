package com.example.demo.handlers;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;

import java.util.Collections;


public class DeleteClientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String numeroIdentificacion = request.getPathParameters().get("NumeroIdentificacion");

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName("Cliente")
                .withKey(Collections.singletonMap("NumeroIdentificacion", new AttributeValue().withS(numeroIdentificacion)));

        dynamoDb.deleteItem(deleteItemRequest);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(204);
        return response;
    }
}

