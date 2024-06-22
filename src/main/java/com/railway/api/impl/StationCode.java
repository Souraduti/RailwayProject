package com.railway.api.impl;

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

public class StationCode  implements  ApiExecutor{
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

            String sql = """
                SELECT
                    station_code,station_name
                FROM station
                WHERE station_name LIKE ?
                """;
            List<Object> params = new ArrayList<>();
            params.add(parameters.get("st_name").toUpperCase()+"%");
            ResultSet resultSet = DButility.selectQuery(sql,params);
            JSONObject responseBody = new JSONObject();
            JSONArray stations = new JSONArray();
            while(resultSet.next()){
                JSONObject station = new JSONObject();
                station.put("st_code",resultSet.getString("station_code"));
                station.put("st_name",resultSet.getString("station_name"));
                stations.put(station);
            }
            responseBody.put("stations",stations);
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }
}
