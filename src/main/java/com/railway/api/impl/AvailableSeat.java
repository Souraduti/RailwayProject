package com.railway.api.impl;

import com.railway.api.ResponseCreator;
import com.railway.api.ResponseStatus;
import com.railway.utility.DButility;
import com.railway.utility.Utility;
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
                        MIN(available_seat) AS seat_count,
                        MAX(waiting) AS waiting_list,
                        seat_class
                    FROM
                        seat_capacity
                    WHERE
                        train_no = ?
                        AND departure_date = ?
                        AND stoppage_no BETWEEN ? AND ?
                    GROUP BY
                        seat_class;
                    """;
            List<Object> params = new ArrayList<>();
            params.add(Integer.parseInt(parameters.get("trainID")));
            params.add(Utility.toDate(parameters.get("departure_date")));
            params.add(Integer.parseInt(parameters.get("boarding")));
            params.add(Integer.parseInt(parameters.get("deboarding")));
            ResultSet resultSet = DButility.selectQuery(sql,params);
            JSONObject responseObject = new JSONObject();
            while (resultSet.next()){
                JSONObject seatInfo = new JSONObject();
                int seatCount =  resultSet.getInt("seat_count");
                int waitingList = resultSet.getInt("waiting_list");
                String availability = (seatCount>0)?"available":"waiting List";
                int count = (seatCount>0)?seatCount:waitingList;
                seatInfo.put("availability",availability);
                seatInfo.put("count",count);
                responseObject.put(resultSet.getString("seat_class"),seatInfo);
            }
            return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }
    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
