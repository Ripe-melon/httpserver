package com.gustaf.shared.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static String url = "jdbc:postgresql://localhost:5432/blogdb";
    private static String username = "postgres";
    private static String password = "password123";

    public Database() {
    }

    public Connection getConnection() throws SQLException {
        Connection connection = null;

        connection = DriverManager.getConnection(url, username, password);

        return connection;
    }
}
