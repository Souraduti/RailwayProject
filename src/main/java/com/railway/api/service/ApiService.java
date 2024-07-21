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
    private final static Map<String, List<ApiDetails>> apiMap = new HashMap<>();

    private Map<String, String> prepareParameter(HttpServletRequest request, JSONObject requestBody, MultivaluedMap<String, String> formParams) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerKey = headerNames.nextElement().toString();
            parameters.put(headerKey, request.getHeader(headerKey));
        }
        if(formParams!=null) formParams.forEach((key, value) -> parameters.put(key, value.get(0)));
        Map<String, String[]> queryParameters = request.getParameterMap();
        queryParameters.forEach((K, V) -> parameters.put(K, V[0]));
        if (requestBody == null) return parameters;
        parameters.put("requestBody", requestBody.toString(0));
        for(String key:requestBody.keySet()){
            String value = requestBody.get(key).toString();
            parameters.put(key,value);
        }
        return parameters;
    }

    private String authorize(HttpServletRequest request, String endPoint) {
        String[] tokenLessEndpoints = {"signup", "login", "route", "suggestion"};
        for (String e : tokenLessEndpoints) {
            if (e.equals(endPoint)) {
                return "_NotRequired";
            }
        }
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        if (!TokenUtil.validateToken(token)) {
            return null;
        }
        return TokenUtil.getUsernameFromToken(token);
    }

    public Response callAPI(HttpServletRequest request, JSONObject requestBody, MultivaluedMap<String, String> formParams, String httpMethod, String url) throws Exception {
        String endpoint = getEndpoint(url);
        //System.out.println("url = " + url);
        //System.out.println("endpoint = " + endpoint);
        String searchKey = httpMethod + ":" + endpoint;
        //System.out.println("searchKey = " + searchKey);
        List<ApiDetails> apiList = apiMap.getOrDefault(searchKey, null);
        boolean inCache = true;
        if (apiList == null) {
            inCache = false;
            apiList = fetchAPIDetails(endpoint, httpMethod);
        }
        if (apiList.size() == 0) {
            return ResponseCreator.sendResponse(null, ResponseStatus.CLASS_NOT_FOUND);
        }
        if (!inCache) {
            apiMap.put(searchKey, apiList);
        }
        /*String authRes = authorize(request,endpoint);
        if(authRes==null){
            //System.out.println("Authorization Fail");
            return ResponseCreator.sendResponse(null, ResponseStatus.AUTHORIZATION_FAIL);
        }*/
        Map<String, String> parameters = prepareParameter(request, requestBody, formParams);
        //parameters.put("u_email",authRes);
        ApiDetails api = null;
        String[] urlParts = url.split("/");
        for (ApiDetails apiDetails : apiList) {
            boolean found = true;
            String apiEndpoint = apiDetails.getEndpoint();
            String[] apiEndpointArray = apiEndpoint.split("/");

            if (apiEndpointArray.length != urlParts.length) {
                continue;
            }
            Map<String, String> pathParameters = new HashMap<>();
            for (int i = 0; i < apiEndpointArray.length; i++) {
                if (apiEndpointArray[i].equals(urlParts[i])) continue;
                if (apiEndpointArray[i].startsWith("{") && apiEndpointArray[i].endsWith("}")) {
                    String pathParam = apiEndpointArray[i].substring(1, apiEndpointArray[i].length() - 1);

                    //System.out.println("pathParam:"+pathParam);
                    //System.out.println("param Value:" + urlParts[i]);
                    pathParameters.put(pathParam, urlParts[i]);
                } else {
                    found = false;
                    break;
                }
            }
            if (found) {
                for (String k : pathParameters.keySet()) {
                    //System.out.println("K="+k);
                    //System.out.println("V="+pathParameters.get(k));
                    parameters.put(k, pathParameters.get(k));
                }
                api = apiDetails;
                break;
            }
        }
        if (api == null) {
            return ResponseCreator.sendResponse(new JSONObject(), ResponseStatus.CLASS_NOT_FOUND);
        }

        List<String> requiredParameters = api.getRequiredParams();
        for (String params : requiredParameters) {
            //System.out.println("params = " + params);
            if (!parameters.containsKey(params)) {
                JSONObject responseBody = new JSONObject();
                responseBody.put("missingParameter", params);
                return ResponseCreator.sendResponse(responseBody, ResponseStatus.REQUIRED_PARAMETER_MISSING);
            }
        }
        Class<?> apiClass = Class.forName(api.getImplementationClass());
        ApiExecutor apiExecutor = (ApiExecutor) apiClass.getDeclaredConstructor().newInstance();
        Response response = apiExecutor.validate(parameters);
        if (response != null) {
            return response;
        }
        return apiExecutor.execute(parameters);
    }

    private List<ApiDetails> fetchAPIDetails(String endpoint, String httpMethod) throws Exception {
        String sql = """
                SELECT *
                FROM restapi_map
                WHERE endpoint LIKE ? AND api_method = ?
                """;
        //System.out.println("endpoint = " + endpoint);
        //System.out.println("httpMethod = " + httpMethod);
        ResultSet resultSet = DButility.selectQuery(sql, Arrays.asList(endpoint + "%", httpMethod));
        List<ApiDetails> apiDetails = new ArrayList<>();
        while (resultSet.next()) {
            String url = resultSet.getString("endpoint");
            String required_paramsCSV = resultSet.getString("required_parameters");
            List<String> required_params;
            if (required_paramsCSV == null) {
                required_params = new ArrayList<>();
            } else {
                required_params = Arrays.asList(required_paramsCSV.split(","));
            }
            String implementationClass = resultSet.getString("api_class_name");
            apiDetails.add(new ApiDetails(url, httpMethod, required_params, implementationClass));
        }
        //System.out.println("Size : "+apiDetails.size());
        return apiDetails;
    }

    private String getEndpoint(String generic) {
        String endpoint = generic;
        int index = generic.indexOf("/");
        if (index != -1) {
            endpoint = generic.substring(0, index);
        }
        return endpoint;
    }
}