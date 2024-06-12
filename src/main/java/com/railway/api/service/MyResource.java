package com.railway.api.service;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
*  Create an Admin Page -> will call a Post Api
* initialize log4j
*/

@Path("/root")
public class MyResource {
    @Path("{generic:.*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response endpointGet(@Context HttpServletRequest request, @PathParam("generic") String generic) {
        try {
            //request.getSession();
            ApiService apiService = new ApiService();
            return apiService.callAPI(request, null, "GET",generic);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Catching "+e.getMessage());
            JSONObject responseBody = new JSONObject();
            responseBody.put("error",e.getMessage());
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.ERROR);
        }
    }

    @POST
    @Path("{generic:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response endpointPost(@Context HttpServletRequest request, @PathParam("generic") String generic, MultivaluedMap<String, String> formParam) {
        System.out.println("POST : Multipart_from_Data");
        formParam.forEach((K,V)->{
            System.out.print(K+"  : ");
            for (int i = 0; i < V.size(); i++) {
                System.out.print(V.get(i)+" , ");
            }
            System.out.print("\n");
        });
        try {
            ApiService apiService = new ApiService();
            return apiService.callAPI(request, formParam, "POST",generic);
        }
        catch (Exception e){
            System.out.println("Catching "+e.getMessage());
            JSONObject responseBody = new JSONObject();
            responseBody.put("error",e.getMessage());
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.ERROR);
        }
    }

    @Path("{generic:.*}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response endPointPOST(@Context HttpServletRequest request, @PathParam("generic") String generic, MultivaluedMap<String, String> formParam) {
        System.out.println("POST : Application_form_urlencoded");
        try {
            ApiService apiService = new ApiService();
            return apiService.callAPI(request, formParam, "POST",generic);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Catching "+e.getMessage());
            JSONObject responseBody = new JSONObject();
            responseBody.put("error",e.getMessage());
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.ERROR);
        }
    }
    @POST
    @Path("{generic:.*}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response endpointPost(@Context HttpServletRequest request) {
        System.out.println("POST : Application/json");
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            System.out.println("json = " + json);
            JSONObject jsonObject = new JSONObject(json);
            ApiService apiService = new ApiService();
            return apiService.callAPI(request,null, "POST","");
        }
        catch (Exception e){
            System.out.println("Catching "+e.getMessage());
            JSONObject responseBody = new JSONObject();
            responseBody.put("error",e.getMessage());
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.ERROR);
        }
    }

    @POST
    @Path("/admin/{generic:.*}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminEndpoint(@Context HttpServletRequest request,@PathParam("generic") String generic,MultivaluedMap<String,String> formParam){
        try {
            ApiService apiService = new ApiService();
            return apiService.callAPI(request, formParam, "POST",generic);
        }
        catch (Exception e){
            System.out.println("Catching "+e.getMessage());
            JSONObject responseBody = new JSONObject();
            responseBody.put("error",e.getMessage());
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.ERROR);
        }
    }
}
