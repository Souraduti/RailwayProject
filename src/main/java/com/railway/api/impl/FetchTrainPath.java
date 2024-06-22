package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class FetchTrainPath implements ApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
            String sql =
                    """
                    SELECT train_name,station_name,station.station_code,arrival_time,departure_time,distance
                    FROM train_stoppage
                    INNER JOIN train ON train.train_no = train_stoppage.train_no
                    INNER JOIN station ON train_stoppage.station_code = station.station_code
                    WHERE train.train_no = ?
                    ORDER BY stoppage_no ASC
                    """;
        JSONArray halts = new JSONArray();
        ResultSet resultSet = DButility.selectQuery(sql,List.of(Integer.parseInt(parameters.get("trainID"))));
        String trainName = "NOT_FOUND";
        while (resultSet.next()){
            trainName = resultSet.getString("train_name");
            JSONObject halt = new JSONObject();
            halt.put("station_name",resultSet.getString("station_name"));
            halt.put("station_code",resultSet.getString("station_code"));
            halt.put("arrival_time",resultSet.getString("arrival_time"));
            halt.put("departure_time",resultSet.getString("departure_time"));
            halts.put(halt);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("train_name",trainName);
        responseObject.put("halt_list",halts);
        return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
