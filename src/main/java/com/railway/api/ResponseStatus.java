package com.railway.api;

import org.json.JSONObject;

import javax.ws.rs.core.Response;

public enum ResponseStatus {
    OK {
        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("message","success");
            return responseBody;
        }
        @Override
        public Response.Status getStatus() {
            return Response.Status.OK;
        }
    },
    Success{
        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("message","success");
            return responseBody;
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CREATED;
        }
    },
    REQUIRED_PARAMETER_MISSING{
        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("developerMessage","Required parameter missing");
            responseBody.put("message","Required parameter missing");
            return responseBody;
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.BAD_REQUEST;
        }
    },
    CLASS_NOT_FOUND {
        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("developerMessage","API implementation class not found");
            responseBody.put("userMessage","This API is not implemented");
            responseBody.put("errorCode", 40401);
            return responseBody;
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.NOT_FOUND;
        }
    },
    AUTHORIZATION_FAIL{
        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("developerMessage","Token verification failed");
            return responseBody;
        }
        @Override
        public Response.Status getStatus() {
            return Response.Status.UNAUTHORIZED;
        }
    },
    ERROR{
        @Override
        public Response.Status getStatus() {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }

        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("userMessage","That was not supposed to happen");
            responseBody.put("developerMessage","Logical error in Server Side ");
            return  responseBody;
        }
    },
    CREATED{
        @Override
        public Response.Status getStatus() {
            return  Response.Status.CREATED;
        }

        @Override
        public JSONObject getMessage() {
            JSONObject responseBody = new JSONObject();
            responseBody.put("message","resource created successfully");
            return responseBody;
        }
    };
    public abstract JSONObject getMessage();
    public abstract Response.Status getStatus();
}
