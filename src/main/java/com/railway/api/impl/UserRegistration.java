package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.api.security.PasswordHandler;
import com.railway.utility.DButility;
import com.railway.utility.Utility;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRegistration implements ApiExecutor{
    private boolean emailVerify(){
        return  true;
    }

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql = """
                INSERT INTO customers VALUES
                (?,?,?,?,?,?,?,?)
                """;
        String salt = PasswordHandler.getSalt();
        String password = parameters.get("password");
        String hash = PasswordHandler.getHashedPassword(password,salt);
        List<Object> params = new ArrayList<>();
        params.add(parameters.get("email"));
        params.add(parameters.get("name"));
        params.add(parameters.get("gender"));
        params.add(parameters.get("address"));
        params.add(hash);
        params.add(true);
        params.add(Utility.toDate(parameters.get("dob")));
        params.add(salt);

        JSONObject responseBody = new JSONObject();
        try {
            DButility.otherQuery(sql,params);
        } catch (Exception e) {
            responseBody.put("signup_status","failed");
            responseBody.put("cause","Email already in use");
            //System.out.println(e.getMessage());
            e.printStackTrace();
        }
        if(!responseBody.has("signup_status")) responseBody.put("signup_status","success");
        return ResponseCreator.sendResponse(responseBody, ResponseStatus.CREATED);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }

}
