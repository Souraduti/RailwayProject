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

public class UserRegistration implements ApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql = """
                INSERT INTO customers VALUES
                (?,?,?,?,?,?,CAST(? AS DATE))
                """;

        List<Object> params = new ArrayList<>();
        params.add(parameters.get("email"));
        params.add(parameters.get("name"));
        params.add(parameters.get("gender"));
        params.add(parameters.get("address"));
        params.add(parameters.get("password"));
        params.add(true);
        params.add(parameters.get("dob"));

        DButility.otherQuery(sql,params);

        JSONObject responseBody = new JSONObject();
        responseBody.put("status","success");
        return ResponseCreator.sendResponse(responseBody, Response.Status.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {

        String email = parameters.get("email");
        System.out.println("email = " + email);
        String sql = """
                SELECT COUNT(*)
                FROM customers
                WHERE email = ?
                """;
        ResultSet resultSet = DButility.selectQuery(sql,List.of(email));
        if(resultSet.next()){
            int emailCount = resultSet.getInt(1);
            System.out.println("emailCount = " + emailCount);
            if(emailCount!=0){
                JSONObject responseObject = new JSONObject();
                responseObject.put("Registration-status","failed");
                responseObject.put("cause","email already in use");
                return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
            }
        }
        return null;
    }

}
