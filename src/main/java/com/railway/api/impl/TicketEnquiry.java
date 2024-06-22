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

public class TicketEnquiry implements ApiExecutor{
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql =
                """
                    SELECT passenger_details.train_no,train_name,p_name,sl_no,reservation_status,seat_no
                    FROM passenger_details
                    JOIN train
                    ON passenger_details.train_no = train.train_no
                    JOIN train_stoppage t1
                    ON passenger_details.train_no = t1.train_no
                    JOIN train_stoppage t2
                    ON passenger_details.train_no = t2.train_no
                    WHERE passenger_details.booking_id = ?
                    AND passenger_details.boarding_stoppage_no = t1.stoppage_no
                    AND passenger_details.deboarding_stoppage_no = t2.stoppage_no
                  """;
        ResultSet resultSet = DButility.selectQuery(sql, List.of(parameters.get("ticket_no")));

        String trainName = null;
        int trainNo=0;
        JSONObject responseBody = new JSONObject();
        JSONArray passengers = new JSONArray();
        while (resultSet.next()){
            trainName = resultSet.getString("train_name");
            trainNo = resultSet.getInt("train_no");
            JSONObject passenger = new JSONObject();
            passenger.put("sl_no",resultSet.getString("sl_no"));
            passenger.put("name",resultSet.getString("p_name"));
            passenger.put("status",resultSet.getString("reservation_status"));
            passenger.put("seat_no",resultSet.getString("seat_no"));
            passengers.put(passenger);
        }
        if(trainName==null){
            responseBody.put("problem","your ticket number is wrong");
        }else {
            responseBody.put("train_number",trainNo);
            responseBody.put("train_name",trainName);
            responseBody.put("passengers",passengers);
        }
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }
}
