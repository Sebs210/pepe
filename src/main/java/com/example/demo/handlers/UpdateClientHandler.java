package com.example.demo.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UpdateClientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String numeroIdentificacion = request.getPathParameters().get("NumeroIdentificacion");

        // Obtener el cuerpo de la solicitud
        String requestBody = request.getBody();

        // Convertir el JSON en un mapa de atributos
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> attributeValuesMap;
        try {
            attributeValuesMap = objectMapper.readValue(requestBody, new TypeReference<Map<String, Object>>(){});
        } catch (IOException e) {
            context.getLogger().log("Error al convertir el cuerpo de la solicitud en un mapa de atributos: " + e.getMessage());
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(400);
            response.setBody("Error al convertir el cuerpo de la solicitud en un mapa de atributos");
            return response;
        }

        // Crear un mapa de valores de atributo con los datos del cliente que deseamos actualizar
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        for (Map.Entry<String, Object> entry : attributeValuesMap.entrySet()) {
            String attributeName = entry.getKey();
            Object attributeValue = entry.getValue();
            attributeValues.put(":" + attributeName, new AttributeValue(attributeValue.toString()));
        }

        // Crear la expresión de actualización con los nombres de los atributos y los valores de atributo correspondientes
        StringBuilder updateExpressionBuilder = new StringBuilder("SET ");
        for (Map.Entry<String, AttributeValue> entry : attributeValues.entrySet()) {
            String attributeName = entry.getKey().substring(1); // Quita el ':' del nombre del atributo
            updateExpressionBuilder.append(attributeName).append(" = :").append(attributeName).append(", ");
        }
        updateExpressionBuilder.delete(updateExpressionBuilder.length() - 2, updateExpressionBuilder.length()); // Elimina la última coma y el espacio
        String updateExpression = updateExpressionBuilder.toString();

        // Crear la solicitud de actualización
        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("Cliente")
                .withKey(Collections.singletonMap("NumeroIdentificacion", new AttributeValue(numeroIdentificacion)))
                .withUpdateExpression(updateExpression)
                .withExpressionAttributeValues(attributeValues);

        // Ejecutar la solicitud de actualización
        dynamoDb.updateItem(updateItemRequest);

        // Crear y retornar la respuesta
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("Cliente actualizado correctamente");
        return response;
    }
}
