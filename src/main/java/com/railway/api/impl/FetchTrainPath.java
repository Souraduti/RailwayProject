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

public class FetchTrainPath implements ApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {
            List<String> halts = new ArrayList<>();
            String sql =
                    """
                    SELECT station_name
                    FROM train_stoppage
                    INNER JOIN train ON train.train_no = train_stoppage.train_no
                    INNER JOIN station ON train_stoppage.station_code = station.station_code
                    WHERE train.train_no = ?
                    ORDER BY stoppage_no ASC
                    """;

            ResultSet resultSet = DButility.selectQuery(sql,List.of(Integer.parseInt(parameters.get("trainID"))));
            while (resultSet.next()){
                halts.add(resultSet.getString("station_name"));
            }
            JSONObject responseObject = new JSONObject();
            responseObject.put("halt_list", new JSONArray(halts));
            return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
