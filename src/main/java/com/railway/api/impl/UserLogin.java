package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.api.security.TokenUtil;
import com.railway.utility.DButility;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class UserLogin implements ApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

        String email = parameters.get("email");
        String password = parameters.get("password");
        String storedPassword = "";
        String sql = """
                SELECT password
                FROM customers
                WHERE email = ?
                """;
        ResultSet resultset =  DButility.selectQuery(sql, List.of(email));
        if(resultset.next()){
            storedPassword = resultset.getString("password");
        }
        JSONObject responseObject = new JSONObject();
        if(!password.equals(storedPassword)){
            responseObject.put("login-status","loginFailed");
        }else{
            String token  = TokenUtil.generateToken(email);
            responseObject.put("login-status","success");
            responseObject.put("token",token);
        }
        return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
