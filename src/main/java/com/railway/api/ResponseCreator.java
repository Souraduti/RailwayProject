package com.railway.api;

import org.json.JSONObject;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import java.util.Iterator;

public class ResponseCreator {
    public static Response sendResponse(Object responseBody, Status status) {
        return Response.status(status).entity(responseBody.toString()).build();
    }

    public static Response sendResponse(JSONObject responseObject, ResponseStatus status) {
        if(responseObject==null){
            responseObject = new JSONObject();
        }
        JSONObject statusMessage = status.getMessage();
        Iterator<String> keys = statusMessage.keys();
        while (keys.hasNext()){
            String key = keys.next();
            responseObject.put(key,statusMessage.get(key));
        }
        return Response.status(status.getStatus()).entity(responseObject.toString()).build();
    }
}
