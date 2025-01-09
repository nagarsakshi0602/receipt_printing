package com.example.receiptprinting.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    private static final String DB_URL;
    private static final String DB_USERNAME;
    private static final String DB_PASSWORD;

    static {
        PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
        try {
            propertyFileLoader.loadProperty("config");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DB_URL = propertyFileLoader.getProperty("db_url");
        DB_USERNAME = propertyFileLoader.getProperty("db_username");
        DB_PASSWORD = propertyFileLoader.getProperty("db_password");
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
