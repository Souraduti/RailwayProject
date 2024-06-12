package com.railway.api.impl.adminapis;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllEndpoint implements AdminApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
           List<JSONObject> endpoints = new ArrayList<>();
           String sql = """
                   SELECT * FROM restapi_map
                   """;
           ResultSet rs = DButility.selectQuery(sql);
           while (rs.next()) {
               JSONObject endpoint = new JSONObject();
               endpoint.put("endpoint",rs.getString("endpoint"));
               endpoint.put("api_class_name",rs.getString("api_class_name"));
               String requiredParamsCsv = rs.getString("required_parameters");
               if(requiredParamsCsv!=null){
                   endpoint.put("required_parameters",new JSONArray(requiredParamsCsv.split(",")));
               }
               endpoints.add(endpoint);
           }
           JSONObject responseObject = new JSONObject();
           responseObject.put("Endpoint-List", new JSONArray(endpoints));
           return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
