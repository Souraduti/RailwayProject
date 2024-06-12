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

public class AvailableSeat implements  ApiExecutor{
    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

            String sql = """
                    SELECT
                        src.train_no,
                        src.stoppage_no,
                        dst.stoppage_no,
                        (
                            SELECT MIN(available_capacity)
                            FROM seat_capacity
                            WHERE train_no = src.train_no
                            AND stoppage_no BETWEEN src.stoppage_no AND dst.stoppage_no
                        ) AS available_seat
                    FROM train_stoppage src
                    JOIN train_stoppage dst ON src.train_no = dst.train_no
                    WHERE dst.train_no = ?
                    AND src.station_code = ?
                    AND dst.station_code = ?;                    
                    """;
            List<Object> params = new ArrayList<>();
            params.add(Integer.parseInt(parameters.get("trainID")));
            params.add(parameters.get("boarding"));
            params.add(parameters.get("deboarding"));
            ResultSet resultSet = DButility.selectQuery(sql,params);
            JSONObject responseObject = new JSONObject();
            int available_seat = resultSet.next()?resultSet.getInt("available_seat"):0;
            responseObject.put("available_seats",available_seat);
            return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
