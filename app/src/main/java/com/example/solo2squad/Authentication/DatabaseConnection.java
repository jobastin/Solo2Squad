package com.example.solo2squad.Authentication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:mysql://<RDS_ENDPOINT>:<PORT>/<DATABASE_NAME>";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "S0l02Squad123!";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
