package com.myapp.aws.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.myapp.aws.workflow.WorkflowServiceImpl;

import java.util.HashMap;
import java.util.Map;

public class WorkflowHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context)  {
        LambdaLogger logger = context.getLogger();
        logger.log("CONTEXT: " + gson.toJson(context));
        logger.log("EVENT: " + gson.toJson(event));

        String httpMehod = event.getHttpMethod();
        String path = event.getPath();
        String s3_bucket_name = System.getenv("S3_BUCKET_NAME");

        Map params = null;
        System.out.println("Path: " + path);

        if(httpMehod.equalsIgnoreCase("POST")){
            String body = event.getBody();
            JsonObject bodyJson = new Gson().fromJson(body, JsonObject.class);
            Gson gson = new Gson();
            params = gson.fromJson(bodyJson, HashMap.class);
        }else if(httpMehod.equalsIgnoreCase("GET")){
            params = event.getQueryStringParameters();
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        if(params != null){
            String responseJson = processRequest(s3_bucket_name, path, params);
            if(responseJson == null){
                response.setStatusCode(404);
                response.setBody("Unable to process request: Unknown endpoint");
            }
            response.setBody(responseJson);

            response.setIsBase64Encoded(false);
            response.setStatusCode(200);

        }else{
            response.setStatusCode(404);
            response.setBody("Unable to process request: Empty params");
        }

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        response.setHeaders(headers);

        return response;
    }

    private String processRequest(String s3_bucket_name, String path, Map params){
        System.out.println("Processing requests with params: " + params);
        String responseJson = null;
        if(path != null) {
            path = path.substring(1, path.length());
            if (path.equalsIgnoreCase("getDecision")) {
                WorkflowServiceImpl service = new WorkflowServiceImpl();
                Map result = service.processRules(s3_bucket_name, params);

                responseJson = gson.toJson(result);
            }
        }
        return responseJson;
    }
}
