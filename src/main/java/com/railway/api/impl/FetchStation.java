package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class FetchStation implements  ApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql = """
                SELECT station_name
                FROM station
                WHERE station_code = ?
                """;
        ResultSet resultSet = DButility.selectQuery(sql, List.of(parameters.get("stationID")));
        JSONObject responseBody = new JSONObject();
        if(resultSet.next()){
            responseBody.put("station_name",resultSet.getString("station_name"));
        }else{
            responseBody.put("station_name","STATION NOT FOUND");
        }
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
