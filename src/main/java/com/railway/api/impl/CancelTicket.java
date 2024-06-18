package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.databaseconnections.Connector;
import com.railway.utility.DButility;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CancelTicket implements  ApiExecutor{
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String insertSql =
                """
                       INSERT INTO booking_queue
                       (
                            id,
                            u_email,
                            train_no,
                            boarding_stoppage_no,
                            deboarding_stoppage_no,
                            departure_date,
                            is_cancel,
                            booking_time,
                            t_count
                       )
                       SELECT DISTINCT
                            booking_id,
                            u_email,
                            train_no,
                            boarding_stoppage_no,
                            deboarding_stoppage_no,
                            dep_date,
                            TRUE,
                            ?,?
                       FROM passenger_details
                       WHERE booking_id = ?
                        """;
        JSONObject requestBody = new JSONObject(parameters.get("requestBody"));
        String ticketNumber = requestBody.getString("ticket_no");
        String updateSql = """
                UPDATE passenger_details
                SET cancel_request = true
                WHERE booking_id = ? AND sl_no = ?
                """;
        List<List<Object>> params = new ArrayList<>();
        JSONArray cancelList =  requestBody.getJSONArray("sl_no");
        for(int i =0;i<cancelList.length();i++){
            int serialNumber = cancelList.getInt(i);
            params.add(Arrays.asList(ticketNumber,serialNumber));
        }

        JSONObject responseBody = new JSONObject();
        try (Connection con = Connector.getConnection()){
            con.setAutoCommit(false);
            DButility.otherQuery(con,insertSql,Arrays.asList(System.currentTimeMillis(),cancelList.length(),ticketNumber));
            DButility.batchQuery(con,updateSql,params);
            con.commit();
            responseBody.put("cancellation_status","request_processed");
        } catch (Exception e) {
            responseBody.put("cancellation_status","request_incomplete");
            e.printStackTrace();
        }
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }
}

