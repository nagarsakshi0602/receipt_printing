package com.example.receiptprinting.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        System.out.println("Intializing DB");
        try (Connection conn = DatabaseConnectionManager.getConnection(); Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                        CREATE TABLE IF NOT EXISTS donators_details (
                            id INT PRIMARY KEY,
                            name VARCHAR(100),
                            address VARCHAR(100),
                            mobile_no VARCHAR(15),
                            email_id VARCHAR(100),
                            amount DOUBLE,
                            mode_of_payment VARCHAR(100),
                            payment_details VARCHAR(50),
                            date DATE,
                            aadhar_no VARCHAR(100),
                            remark VARCHAR(100)
                            
                        );
                    """;
            stmt.execute(createTableSQL);
            System.out.println("Intialization Done");

            //Create statement if it doesn't exist
            String createSequenceSQL = "CREATE SEQUENCE IF NOT EXISTS public.receipt_no " +
                    "START WITH " + PropertyFileLoader.getInstance().getProperty("starting_id")+ "INCREMENT BY 1";
            stmt.execute(createSequenceSQL);

            // Check if the table is empty
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS count FROM donators_details");
            resultSet.next();
            int rowCount = resultSet.getInt("count");

            if (rowCount == 0) {
                // If the table is empty, set the auto-increment starting value to requested value
                stmt.execute("ALTER SEQUENCE receipt_no RESTART WITH " + PropertyFileLoader.getInstance().getProperty("starting_id"));
                System.out.println("Table is empty. Setting starting value to " + PropertyFileLoader.getInstance().getProperty("starting_id"));
            } else {
                System.out.println("Table is not empty. Auto-increment continues as usual.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
