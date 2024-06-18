package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StationSuggestion implements ApiExecutor {
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

        String sql  = """
                SELECT station_name,station_code
                FROM  station
                WHERE LOWER(station_name) like LOWER(?);
                """;

        String query =  parameters.get("query")+"%";
        ResultSet resultSet =  DButility.selectQuery(sql, List.of(query));

        List<JSONObject> suggestions = new ArrayList<>();
        while (resultSet.next()){
            JSONObject suggestion = new JSONObject();
            suggestion.put("station_name",resultSet.getString("station_name"));
            suggestion.put("station_code",resultSet.getString("station_code"));
            suggestions.add(suggestion);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("suggestion_list",new JSONArray(suggestions));
        return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
