package com.railway.api.impl.adminapis;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AddEndpoint implements  AdminApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
        String sql = "INSERT  INTO restapi_map VALUES (?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add(parameters.get("endpoint"));
        params.add(parameters.get("class_path"));
        params.add(parameters.get("api_method"));
        params.add(parameters.get("required_params"));
        int rowsAffected = DButility.otherQuery(sql,params);
        return ResponseCreator.sendResponse(null, ResponseStatus.CREATED);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
