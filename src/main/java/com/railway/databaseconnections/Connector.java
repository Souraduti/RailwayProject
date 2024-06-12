package com.railway.databaseconnections;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connector {
    private static final String databaseUrl = "jdbc:postgresql://localhost:5432/postgres?currentSchema=railway";
    private static  final String user = "postgres";
    private static  final  String password = "admin";

    public static Connection getConnection()throws Exception{
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(databaseUrl, user, password);
    }
}
