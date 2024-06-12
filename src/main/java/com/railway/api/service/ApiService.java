package com.railway.api.service;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.api.impl.ApiExecutor;
import com.railway.api.security.TokenUtil;
import com.railway.utility.DButility;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.*;

public class ApiService {
    private final static Map<String,List<ApiDetails>> apiMap = new HashMap<>();

    private Map<String, String> prepareParameter(HttpServletRequest request, MultivaluedMap<String,String> formParam){
        Map<String, String> parameters = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String headerKey = headerNames.nextElement().toString();
            parameters.put(headerKey, request.getHeader(headerKey));
        }

        Map<String, String[]> queryParameters = request.getParameterMap();
        queryParameters.forEach((K,V)->{
            parameters.put(K,V[0]);
        });
        if(formParam==null) return parameters;
        formParam.forEach((K,V)->{
            parameters.put(K,V.get(0));
        });
        return parameters;
    }
   /* private String findAPIClass(String endPoint,Map<String,String> parameters) throws  Exception{
        String sql = "select api_class_name,required_parameters from restapi_map where endpoint = ?" ;
        List<Object> params = new ArrayList<>();
        params.add(endPoint);
        ResultSet resultSet = DButility.selectQuery(sql,params);
        if(!resultSet.next()) return null;
        String className =  resultSet.getString("api_class_name");
        String paramsListCSV = resultSet.getString("required_parameters");
        String [] paramList = {};
        if(paramsListCSV!=null) paramList = paramsListCSV.split(",");
        for(String parameter:paramList){
            if(parameters.get(parameter)==null){
                className = "ParameterMissing"+parameter;
                break;
            }
        }
        return  className;
    }*/
    private  String authorize(HttpServletRequest request,String endPoint){
        String [] tokenLessEndpoints = {"signup","login","route","/suggestion"};
        for (String e : tokenLessEndpoints){
            if(e.equals(endPoint)) {
                return  "_NotRequired";
            }
        }
        String authorizationHeader  = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            //System.out.println("Unauthorized access");
            return null;
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if(!TokenUtil.validateToken(token)){
            return null;
        }
        return  TokenUtil.getUsernameFromToken(token);
    }
    /*public Response callAPI(HttpServletRequest request, JSONObject requestBody,String endPoint,MultivaluedMap<String,String> formParam)throws Exception{
        String authRes = authorize(request,endPoint);
        if(authRes==null){
            return ResponseCreator.sendResponse(new JSONObject(), Response.Status.UNAUTHORIZED);
        }
        Map<String,String> params = prepareParameter(request,formParam);
        String className = findAPIClass(endPoint,params);
        if(className==null){
            return ResponseCreator.sendResponse(null, ResponseStatus.CLASS_NOT_FOUND);
        }
        if(className.startsWith("ParameterMissing")){
            JSONObject responseBody = new JSONObject();
            responseBody.put("parameterMissing",className.substring("ParameterMissing".length()));
            return  ResponseCreator.sendResponse(responseBody, Response.Status.BAD_REQUEST);
        }
        if(!authRes.equals("_NotRequired")){
            params.put("email",authRes);
        }
        Class<?> apiClass = Class.forName(className);
        ApiExecutor apiExecutor = (ApiExecutor) apiClass.getDeclaredConstructor().newInstance();
        Response response = apiExecutor.validate(params);
        if(response!=null){
                return response;
            }
        return apiExecutor.execute(params);
    }*/
    /*public Response callAdminAPI(HttpServletRequest request, JSONObject requestBody, String endPoint, MultivaluedMap<String,String> formParam) throws Exception{
        Map<String,String> params = prepareParameter(request,formParam);
        String className = findAPIClass(endPoint,params);
        if(className==null){
            return ResponseCreator.sendResponse(new JSONObject(), Response.Status.NOT_FOUND);
        }
        if(className.startsWith("ParameterMissing")){
            JSONObject responseBody = new JSONObject();
            System.out.println("Parameter Missing");
            responseBody.put("parameterMissing",className.substring("ParameterMissing".length()));
            return  ResponseCreator.sendResponse(responseBody, Response.Status.BAD_REQUEST);
        }
        Class<?> apiClass = Class.forName(className);
        AdminApiExecutor adminApiExecutor = (AdminApiExecutor) apiClass.getDeclaredConstructor().newInstance();
        Response response = adminApiExecutor.validate(params);
        if(response!=null){
            return response;
        }
        return adminApiExecutor.execute(params);
    }*/
    public Response callAPI(HttpServletRequest request,MultivaluedMap<String,String> formParam,String httpMethod,String url) throws  Exception{
        String endpoint = getEndpoint(url);
        String searchKey = httpMethod+":"+endpoint;
        //System.out.println("searchKey = " + searchKey);
        //System.out.println("url = " + url);
        List<ApiDetails> apiList = apiMap.getOrDefault(searchKey,null);
        boolean inCache = true;
        if(apiList==null){
            //System.out.println("Not in Cache");
            inCache = false;
            apiList = fetchAPIDetails(endpoint,httpMethod);
        }
        if(apiList.size()==0){
            //System.out.println("Empty endpoint List");
            return ResponseCreator.sendResponse(null, ResponseStatus.CLASS_NOT_FOUND);
        }
        if(!inCache){
            apiMap.put(searchKey,apiList);
        }
        String authRes = authorize(request,endpoint);
        if(authRes==null){
            //System.out.println("Authorization Fail");
            return ResponseCreator.sendResponse(null, ResponseStatus.AUTHORIZATION_FAIL);
        }
        Map<String,String> parameters = prepareParameter(request,formParam);
        ApiDetails api = null;
        String [] urlParts = url.split("/");
        /*for (int i = 0; i < urlParts.length; i++) {
            System.out.println("urlParts["+i+"] = " + urlParts[i]);
        }*/
        boolean found = true;
        for(ApiDetails apiDetails:apiList){
            found =  true;
            String  apiEndpoint = apiDetails.getEndpoint();
            //System.out.println("apiEndpoint = " + apiEndpoint);
            String[] apiEndpointArray = apiEndpoint.split("/");
            /*for (int i = 0; i < apiEndpointArray.length; i++) {
                System.out.print(apiEndpointArray[i]+",");
            }*/
            //System.out.print("\n");
            //System.out.println(apiEndpointArray.length);
            //System.out.println(urlParts.length);
            if(apiEndpointArray.length!=urlParts.length){
                //System.out.println("Length not Matching");
                found = false;
                continue;
            }
            Map<String,String> pathParameters = new HashMap<>();
            for(int i = 0;i<apiEndpointArray.length;i++){
                if(apiEndpointArray[i].equals(urlParts[i])) continue;
                //System.out.println("Starts With : "+apiEndpointArray[i].startsWith("{"));
                //System.out.println("Ends With : "+apiEndpointArray[i].endsWith("}"));
                if(apiEndpointArray[i].startsWith("{")&&apiEndpointArray[i].endsWith("}")){
                    //System.out.println("Path param Found");
                    String pathParam = apiEndpointArray[i].substring(1,apiEndpointArray[i].length()-1);
                    pathParameters.put(pathParam,urlParts[i]);
                }
                else {
                    found = false;
                    break;
                }
            }
            if(found){
                //System.out.println("API Found");
                parameters.putAll(pathParameters);
                api = apiDetails;
                break;
            }
        }
        if (!found) {
            return ResponseCreator.sendResponse(new JSONObject(), ResponseStatus.CLASS_NOT_FOUND);
        }

        List<String> requiredParameters = api.getRequiredParams();
        for(String params:requiredParameters){
            //System.out.println("params = " + params);
            if(parameters.get(params)==null){
                JSONObject responseBody = new JSONObject();
                responseBody.put("parameterMissing",params);
                return ResponseCreator.sendResponse(responseBody, ResponseStatus.REQUIRED_PARAMETER_MISSING);
            }
        }
        Class<?> apiClass = Class.forName(api.getImplementationClass());
        ApiExecutor apiExecutor = (ApiExecutor) apiClass.getDeclaredConstructor().newInstance();
        Response response = apiExecutor.validate(parameters);
        if(response!=null){
            return response;
        }
        return apiExecutor.execute(parameters);

    }
    private List<ApiDetails> fetchAPIDetails(String endpoint,String httpMethod) throws Exception{
        String sql = """
                SELECT *
                FROM restapi_map
                WHERE endpoint LIKE ? AND api_method = ?
                """;
        //System.out.println("endpoint = " + endpoint);
        //System.out.println("httpMethod = " + httpMethod);
        ResultSet resultSet = DButility.selectQuery(sql,Arrays.asList(endpoint+"%",httpMethod));
        List<ApiDetails> apiDetails = new ArrayList<>();
        while (resultSet.next()){
            String url = resultSet.getString("endpoint");
            String required_paramsCSV = resultSet.getString("required_parameters");
            List<String> required_params;
            if(required_paramsCSV==null){
                required_params = new ArrayList<>();
            }else {
                required_params = Arrays.asList(required_paramsCSV.split(","));
            }
            String implementationClass = resultSet.getString("api_class_name");
            apiDetails.add(new ApiDetails(url,httpMethod,required_params,implementationClass));
        }
        //System.out.println("Size : "+apiDetails.size());
        return apiDetails;
    }
    private String getEndpoint(String generic){
        String endpoint = generic;
        int index = generic.indexOf("/");
        if(index!=-1){
            endpoint = generic.substring(0,index);
        }
        return endpoint;
    }
}