package com.example.solo2squad.Authentication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:mysql://database-1.cedll4ujrwj2.us-east-1.rds.amazonaws.com:3306/Solo2Squad?useSSL=false";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "S0l02Squad123!";



    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

//        try {
//            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return connection;
    }
}
