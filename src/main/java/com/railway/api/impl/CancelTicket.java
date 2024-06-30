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
                    INSERT INTO booking_queue(id,is_cancel,booking_time) VALUES ( ?,TRUE,?)
                """;
        JSONObject requestBody = new JSONObject(parameters.get("requestBody"));
        String ticketNumber = requestBody.getString("ticket_no");
        String updateSql = """
                UPDATE passenger_details
                SET cancel_request = true
                WHERE booking_id = ? AND sl_no = ? AND u_email = ?;
                """;
        List<List<Object>> params = new ArrayList<>();
        JSONArray cancelList =  requestBody.getJSONArray("sl_no");
        for(int i =0;i<cancelList.length();i++){
            int serialNumber = cancelList.getInt(i);
            params.add(Arrays.asList(ticketNumber,serialNumber,parameters.get("u_email")));
        }

        JSONObject responseBody = new JSONObject();
        try (Connection con = Connector.getConnection()){
            con.setAutoCommit(false);
            int [] res = DButility.batchQuery(con,updateSql,params);
            boolean valid = false;
            for(int it:res){
                if(it!=0){
                    valid = true;
                    break;
                }
            }
            if(valid) DButility.otherQuery(con,insertSql,Arrays.asList(ticketNumber,System.currentTimeMillis()));
            con.commit();
            if(!valid){
                responseBody.put("cancellation_status","request_incomplete");
                responseBody.put("cause","invalid ticket number");
            }else{
                responseBody.put("cancellation_status","request_processed");
            }
        } catch (Exception e) {
            responseBody.put("cancellation_status","request_incomplete");
            responseBody.put("developerMessage",e.getMessage());
            e.printStackTrace();
        }
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.OK);
    }
}