package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import org.json.JSONObject;

import javax.ws.rs.core.Response;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
* this api has to be modified to take station code an return  all station info
*
* */
public class StationCode  implements  ApiExecutor{
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

            String sql = """
                SELECT
                    station_code
                FROM station
                WHERE station_name = ?
                """;
            List<Object> params = new ArrayList<>();
            params.add(parameters.get("st_name"));
            ResultSet resultSet = DButility.selectQuery(sql,params);
            JSONObject station_code = new JSONObject();
            if(!resultSet.next()){
                station_code.put("st_code","Station does not Exist");
            }else{
                station_code.put("st_code",resultSet.getString("station_code"));
            }
            return ResponseCreator.sendResponse(station_code, ResponseStatus.OK);
    }
}
