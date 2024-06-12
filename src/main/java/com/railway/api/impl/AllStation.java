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

public class AllStation implements  ApiExecutor{

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

            List<JSONObject> stations = new ArrayList<>();
            String sql = """
                    SELECT
                        station_name,
                        station_code
                    FROM station
                    """;
            ResultSet resultSet = DButility.selectQuery(sql);
            while (resultSet.next()){
                JSONObject station = new JSONObject();
                station.put("st_name",resultSet.getString("station_name"));
                station.put("st_code",resultSet.getString("station_code"));
                stations.add(station);
            }
            JSONObject responseObject = new JSONObject();
            responseObject.put("total-stations",stations.size());
            responseObject.put("station-list",new JSONArray(stations));
            return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
