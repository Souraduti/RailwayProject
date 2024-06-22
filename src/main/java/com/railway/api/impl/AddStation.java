package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;

public class AddStation implements ApiExecutor{
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql = "INSERT INTO station(station_code,station_name) VALUES (?,?);";
        JSONObject responseBody = new JSONObject();
        try {
            DButility.otherQuery(sql, Arrays.asList(parameters.get("station_code").toUpperCase(),parameters.get("station_name").toUpperCase()));
            responseBody.put("station_add","process_complete");
        } catch (Exception e) {
            responseBody.put("station_add","process_incomplete");
            responseBody.put("cause","station_code already in use");
            responseBody.put("developerMessage",e.getMessage());
            e.printStackTrace();
        }
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.CREATED);
    }
}
