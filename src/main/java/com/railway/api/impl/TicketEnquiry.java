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
                    SELECT
                        pd.train_no,
                        train.train_name,
                        pd.p_name,
                        pd.sl_no,
                        pd.reservation_status,
                        pd.seat_no,
                        pd.departure_date,
                        t1.departure_time,
                        t1.station_code as from,
                        t2.arrival_time,
                        t2.station_code as to
                    FROM   (SELECT train_no,
                                boarding_stoppage_no,
                                deboarding_stoppage_no,
                                p_name,
                                sl_no,
                                reservation_status,
                                seat_no,
                                departure_date
                         FROM   passenger_details
                         WHERE  booking_id = ? ) AS pd
                        INNER JOIN train
                                ON pd.train_no = train.train_no
                        INNER JOIN train_stoppage t1
                                ON pd.train_no = t1.train_no
                        INNER JOIN train_stoppage t2
                                ON pd.train_no = t2.train_no
                                   AND pd.boarding_stoppage_no = t1.stoppage_no
                                   AND pd.deboarding_stoppage_no = t2.stoppage_no
                        """;
        ResultSet resultSet = DButility.selectQuery(sql, List.of(parameters.get("ticket_no")));


        JSONObject responseBody = new JSONObject();
        JSONArray passengers = new JSONArray();
        boolean flag = false;
        responseBody.put("ticket_number",parameters.get("ticket_no"));
        while (resultSet.next()){
            if(!flag){
                responseBody.put("train_number", resultSet.getInt("train_no"));
                responseBody.put("train_name",resultSet.getString("train_name"));
                responseBody.put("departure_date",resultSet.getDate("departure_date"));
                JSONObject tripDetails = new JSONObject();
                tripDetails.put("departure",resultSet.getString("departure_time"));
                tripDetails.put("arrival",resultSet.getString("arrival_time"));
                tripDetails.put("from",resultSet.getString("from"));
                tripDetails.put("to",resultSet.getString("to"));
                responseBody.put("train_trip_details",tripDetails);
                flag = true;
            }

            JSONObject passenger = new JSONObject();
            passenger.put("sl_no",resultSet.getString("sl_no"));
            passenger.put("name",resultSet.getString("p_name"));
            passenger.put("status",resultSet.getString("reservation_status"));
            passenger.put("seat_no",resultSet.getInt("seat_no"));
            passengers.put(passenger);
        }
        if(!flag){
            responseBody.put("user-message","Wrong Ticket number");
            responseBody.put("developer-message","invalid booking_id");
            return ResponseCreator.sendResponse(responseBody,ResponseStatus.INVALID_PARAMETER_VALUE);
        }else {
            responseBody.put("passenger",passengers);
            return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
        }
    }
}
