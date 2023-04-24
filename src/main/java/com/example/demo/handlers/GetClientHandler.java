package com.example.demo.handlers;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;


import java.util.Map;
import java.util.Collections;

public class GetClientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String numeroIdentificacion = request.getPathParameters().get("NumeroIdentificacion");

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName("Cliente")
                .withKey(Collections.singletonMap("NumeroIdentificacion", new AttributeValue().withS(numeroIdentificacion)));

        Map<String, AttributeValue> item = dynamoDb.getItem(getItemRequest).getItem();

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        if (item == null) {
            response.setStatusCode(404);
        } else {
            String responseBody = new Gson().toJson(Map.of(
                    "Nombres", item.get("Nombres").getS(),
                    "Apellidos", item.get("Apellidos").getS(),
                    "NumeroIdentificacion", item.get("NumeroIdentificacion").getS(),
                    "Edad", Integer.parseInt(item.get("Edad").getN()),
                    "CiudadNacimiento", item.get("CiudadNacimiento").getS()
            ));
            response.setStatusCode(200);
            response.setBody(responseBody);
        }
        return response;
    }
}


       

