package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.api.security.PasswordHandler;
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
        String storedPasswordHash = "";
        String salt = "";
        String sql = """
                SELECT password_hash,salt
                FROM customers
                WHERE email = ?
                """;
        ResultSet resultset =  DButility.selectQuery(sql, List.of(email));
        if(resultset.next()){
            storedPasswordHash = resultset.getString("password_hash");
            salt = resultset.getString("salt");
        }
        String generatedPasswordHash = PasswordHandler.getHashedPassword(password,salt);
        JSONObject responseObject = new JSONObject();
        if(!generatedPasswordHash.equals(storedPasswordHash)){
            responseObject.put("login_status","failed");
        }else{
            String token  = TokenUtil.generateToken(email);
            responseObject.put("login_status","success");
            responseObject.put("token",token);
        }
        return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
