package com.example.demo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateClientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, AttributeValue> item = new HashMap<>();

        try {
            Map<String, Object> requestBodyMap = objectMapper.readValue(request.getBody(), new TypeReference<Map<String, Object>>() {});
            item.put("NumeroIdentificacion", new AttributeValue(requestBodyMap.get("NumeroIdentificacion").toString()));
            item.put("Nombres", new AttributeValue(requestBodyMap.get("Nombres").toString()));
            item.put("Apellidos", new AttributeValue(requestBodyMap.get("Apellidos").toString()));
            item.put("TipoIdentificacion", new AttributeValue(requestBodyMap.get("TipoIdentificacion").toString()));
            item.put("Edad", new AttributeValue().withN(requestBodyMap.get("Edad").toString()));
            item.put("CiudadNacimiento", new AttributeValue(requestBodyMap.get("CiudadNacimiento").toString()));
        } catch (IOException e) {
            context.getLogger().log("Error al parsear el cuerpo de la solicitud: " + e.getMessage());
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(400);
            response.setBody("Error al parsear el cuerpo de la solicitud");
            return response;
        }

        PutItemRequest putItemRequest = new PutItemRequest("Cliente", item);
        dynamoDb.putItem(putItemRequest);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(201);
        return response;
    }
}
