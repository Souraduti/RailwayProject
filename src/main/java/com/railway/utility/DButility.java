package com.railway.utility;

import com.railway.databaseconnections.Connector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;


public class DButility {
    private static void fillParameters(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            if(params.get(i) instanceof java.util.Date){
                //System.out.println("Date handled");
                ps.setDate(i+1,new java.sql.Date(((Date) params.get(i)).getTime()));
                continue;
            }
            ps.setObject(i + 1, params.get(i));
        }
    }

    public static ResultSet selectQuery(Connection con, String sql, List<Object> params) throws Exception {

        PreparedStatement ps = con.prepareStatement(sql);
        fillParameters(ps, params);
        return ps.executeQuery();
    }

    public static ResultSet selectQuery(String sql, List<Object> params) throws Exception {
        try (Connection con = Connector.getConnection()) {
            return selectQuery(con, sql, params);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static ResultSet selectQuery(String sql) throws Exception {
        try {
            return selectQuery(sql, new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static int otherQuery(Connection con, String sql, List<Object> params) throws Exception {
        PreparedStatement ps = con.prepareStatement(sql);
        fillParameters(ps, params);
        return ps.executeUpdate();
    }

    public static int otherQuery(String sql, List<Object> params) throws Exception {
        try (Connection con = Connector.getConnection()) {
            return otherQuery(con, sql, params);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static int otherQuery(String sql) throws Exception {
        try (Connection con = Connector.getConnection()) {
            return otherQuery(con, sql, new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static int[] batchQuery(Connection con, String sql, List<List<Object>> params) throws Exception {
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        for (List<Object> param : params) {
            fillParameters(preparedStatement,param);
            preparedStatement.addBatch();
        }
        return preparedStatement.executeBatch();
    }


    public static int[] batchQuery(String sql, List<List<Object>> params) {
        try (Connection con = Connector.getConnection()) {
            return batchQuery(con, sql, params);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static List<Map<String, Object>> getResultAsList(ResultSet resultSet) throws Exception {

        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            int columnCount = metaData.getColumnCount();
            Map<String, Object> row = new LinkedHashMap<>(columnCount);

            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            results.add(row);
        }
        return results;
    }
}