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
import java.util.UUID;


public class BookTicket implements  ApiExecutor {
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        String sql = """
                SELECT COUNT(*) AS isValid
                FROM
                    (SELECT stoppage_no FROM train_stoppage WHERE train_no = ? AND station_code = ?) src,
                    (SELECT stoppage_no FROM train_stoppage WHERE train_no = ? AND station_code = ?) dst
                WHERE src.stoppage_no < dst.stoppage_no;
                """;
        List<Object> params = new ArrayList<>();
        params.add(Integer.parseInt(parameters.get("trainID")));
        params.add(parameters.get("from"));
        params.add(Integer.parseInt(parameters.get("trainID")));
        params.add(parameters.get("to"));
        ResultSet resultSet = DButility.selectQuery(sql, params);
        if (!resultSet.next() || resultSet.getInt("isValid") == 0) {
            JSONObject responseBody = new JSONObject();
            responseBody.put("train_path", "invalid");
            return ResponseCreator.sendResponse(responseBody,Response.Status.OK);
        }
        return null;
    }
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql = """
                INSERT INTO booking_queue (id,train_no, boarding_stoppage_no, deboarding_stoppage_no, departure_date,booking_time,t_count, u_email,completion_status)
                VALUES (
                    ?,
                    ?,
                    (SELECT stoppage_no FROM train_stoppage WHERE train_no = ? AND station_code = ?),
                    (SELECT stoppage_no FROM train_stoppage WHERE train_no = ? AND station_code = ?),
                    CAST(? AS DATE),
                    ?,
                    ?,
                    ?,
                    'Pending'
                );
                """;
        List<Object> params = new ArrayList<>();
        params.add(generateID());
        params.add(Integer.parseInt(parameters.get("trainID")));
        params.add(Integer.parseInt(parameters.get("trainID")));
        params.add(parameters.get("from"));
        params.add(Integer.parseInt(parameters.get("trainID")));
        params.add(parameters.get("to"));
        params.add(parameters.get("dep_date"));
        params.add(System.currentTimeMillis());
        params.add(Integer.parseInt(parameters.get("count")));
        params.add(parameters.get("email"));

        DButility.otherQuery(sql, params);

        JSONObject responseBody = new JSONObject();
        responseBody.put("Booking Status", "successful");
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }
    private String generateID() {
        return UUID.randomUUID().toString();
    }
}