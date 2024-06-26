package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.databaseconnections.Connector;
import com.railway.utility.DButility;
import com.railway.utility.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.*;

public class BookTicket implements  ApiExecutor {

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String id = Utility.generateID();
        String sql =
                """
                INSERT INTO booking_queue (id,booking_time) VALUES (?,?);
                """;
        JSONObject requestBody = new JSONObject(parameters.get("requestBody"));
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(System.currentTimeMillis());

        String passengerSql= """
                    INSERT INTO passenger_details(
                    booking_id,
                    p_name,
                    adhaar_no,
                    gender,
                    dob,
                    train_no,
                    departure_date,
                    seat_class,
                    u_email,
                    boarding_stoppage_no,
                    deboarding_stoppage_no,
                    sl_no
                    )
                    VALUES (?,?,?,?,?,?,?,?,?,
                    (SELECT stoppage_no FROM train_stoppage WHERE train_no = ? AND station_code = ?),
                    (SELECT stoppage_no FROM train_stoppage WHERE train_no = ? AND station_code = ?),
                    ?)
                    """;
        List<List<Object>> sqlParameters = new ArrayList<>();
        JSONArray passengers = requestBody.getJSONArray("passenger_list");
        int sl_no = 1;
        for (Object _passenger: passengers) {

            JSONObject passenger = (JSONObject)_passenger;
            List<Object> passengerParam = new ArrayList<>();

            passengerParam.add(id);
            passengerParam.add(passenger.get("p_name"));
            passengerParam.add(passenger.get("adhaar_no"));
            passengerParam.add(passenger.get("gender"));
            passengerParam.add(Utility.toDate(passenger.getString("dob")));

            passengerParam.add(Integer.parseInt(parameters.get("trainID")));
            passengerParam.add(Utility.toDate(requestBody.getString("dep_date")));
            passengerParam.add(requestBody.getString("seat_class"));
            passengerParam.add(requestBody.get("u_email"));
            passengerParam.add(Integer.parseInt(parameters.get("trainID")));
            passengerParam.add(requestBody.get("from"));
            passengerParam.add(Integer.parseInt(parameters.get("trainID")));
            passengerParam.add(requestBody.get("to"));
            passengerParam.add(sl_no);

            sl_no++;
            sqlParameters.add(passengerParam);
        }

        try(Connection con = Connector.getConnection()){
            con.setAutoCommit(false);
            DButility.otherQuery(con,sql, params);
            DButility.batchQuery(con,passengerSql,sqlParameters);
            con.commit();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }

        JSONObject responseBody = new JSONObject();
        responseBody.put("Booking Status", "successful");
        responseBody.put("ticket_no",id);
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }
}