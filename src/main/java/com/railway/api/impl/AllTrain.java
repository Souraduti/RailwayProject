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

public class AllTrain implements  ApiExecutor{

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

            List<JSONObject> trains = new ArrayList<>();
            String sql = """
                    SELECT
                        train_no,
                        train_name, 
                        train_source, 
                        train_destination
                    FROM train;                
                    """;
            ResultSet resultSet = DButility.selectQuery(sql);
            while (resultSet.next()){
                JSONObject train = new JSONObject();
                train.put("train_no",resultSet.getInt("train_no"));
                train.put("train_name",resultSet.getString("train_name"));
                train.put("train_source",resultSet.getString("train_source"));
                train.put("train_destination",resultSet.getString("train_destination"));
                trains.add(train);
            }
            JSONObject responseObject = new JSONObject();
            responseObject.put("train-details",new JSONArray(trains));
            responseObject.put("total-trains",trains.size());
            return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }
}
