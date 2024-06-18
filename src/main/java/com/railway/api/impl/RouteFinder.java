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

public class RouteFinder implements ApiExecutor {

    @Override
    public Response execute(Map<String, String> parameters) throws Exception {

            List<JSONObject> trains = new ArrayList<>();

            String sql = """
                    SELECT
                        src.train_no,
                        train_name,
                        src.station_code AS src_station_code,
                        dst.station_code AS dst_station_code,
                        src.stoppage_no AS src_stoppage,
                        dst.stoppage_no AS dest_stoppage,
                        (
                            SELECT MIN(available_capacity)
                            FROM seat_capacity
                            WHERE seat_capacity.train_no = src.train_no
                            AND stoppage_no BETWEEN src.stoppage_no AND dst.stoppage_no
                        ) AS available_seats
                    FROM
                        train_stoppage src
                    INNER JOIN
                        train_stoppage dst ON src.train_no = dst.train_no
                    INNER JOIN
                        train ON src.train_no = train.train_no
                    WHERE
                        src.station_code = ?
                        AND dst.station_code = ?
                        AND src.stoppage_no < dst.stoppage_no;
                    """;

            List<Object> params = new ArrayList<>();
            params.add(parameters.get("from"));
            params.add(parameters.get("to"));
            ResultSet resultSet = DButility.selectQuery(sql,params);

            while (resultSet.next()) {
                JSONObject trainDetails = new JSONObject();
                trainDetails.put("train_number", resultSet.getInt("train_no"));
                trainDetails.put("train_name", resultSet.getString("train_name"));
                trainDetails.put("available",resultSet.getInt("available_seats"));
                trains.add(trainDetails);
            }
            JSONObject responseObject = new JSONObject();
            responseObject.put("train_list", new JSONArray(trains));
            return ResponseCreator.sendResponse(responseObject, ResponseStatus.OK);
    }

    @Override
    public Response validate(Map<String, String> parameters) throws Exception {
        return null;
    }
}
