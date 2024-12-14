package com.example.receiptprinting.utils;

import com.example.receiptprinting.models.Donators;

import java.io.IOException;
import java.sql.*;

public class DatabaseUtil {
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

    public static void initializeDatabase() {
        System.out.println("Intializing DB");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                        CREATE TABLE IF NOT EXISTS donators_details (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100),
                            address VARCHAR(100),
                            mobile_no VARCHAR(15),
                            email_id VARCHAR(100),
                            amount DOUBLE,
                            mode_of_payment VARCHAR(100),
                            date DATE,
                            aadhar_no VARCHAR(100),
                            remark VARCHAR(100)
                            
                        );
                    """;
            stmt.execute(createTableSQL);
            System.out.println("Intialization Done");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertDonators(Donators donators) throws SQLException {

        String insertSQL = "INSERT INTO donators_details (name, address, email_id, mobile_no, amount, mode_of_payment, " +
                "date, aadhar_no, remark) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(insertSQL);

        stmt.setString(1, donators.getName());
        stmt.setString(2, donators.getAddress());
        stmt.setString(3, donators.getEmail_id());
        stmt.setString(4, donators.getMobile_no());
        stmt.setDouble(5, donators.getAmount());
        stmt.setString(6, donators.getMode_of_payment());
        stmt.setDate(7, Date.valueOf(donators.getDate()));
        stmt.setString(8, donators.getAadhar_no());
        stmt.setString(9, donators.getRemark());

        stmt.executeUpdate();
    }

    public static int getLastReceiptId() {
        String getSqlQuery = "SELECT TOP 1 ID FROM donators_details ORDER BY ID DESC";

        ResultSet resultSet;
        try {
            resultSet = getConnection().createStatement().executeQuery(getSqlQuery);
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id"));
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static void update(Donators donators, int id) throws SQLException {

        String updateQuery = "UPDATE DONATORS_DETAILS SET NAME = ?, ADDRESS = ?, EMAIL_ID = ?, MOBILE_NO = ?, AMOUNT = ?," +
                "MODE_OF_PAYMENT = ?, DATE = ?, AADHAR_NO = ?, REMARK = ? WHERE ID = ?";

        PreparedStatement ps = getConnection().prepareStatement(updateQuery);

        ps.setString(1, donators.getName());
        ps.setString(2, donators.getAddress());
        ps.setString(3, donators.getEmail_id());
        ps.setString(4, donators.getMobile_no());
        ps.setDouble(5, donators.getAmount());
        ps.setString(6, donators.getMode_of_payment());
        ps.setDate(7, Date.valueOf(donators.getDate()));
        ps.setString(8, donators.getAadhar_no());
        ps.setString(9, donators.getRemark());
        ps.setInt(10, id);

        ps.executeUpdate();

    }

   /* public static void select() {
        System.out.println("Into get donators Details");
        Connection conn = null;
        try {
            conn = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * FROM donators_details");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String phone = resultSet.getString("mobile_no");
                System.out.println("Name: " + name + ", Phone: " + phone);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public static Donators getDonatorsDetail(int id) throws SQLException {
        String getRecordQuery = "SELECT * FROM DONATORS_DETAILS WHERE ID = ?";
        ResultSet rs;
        Donators donators = null;

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);
        ps.setInt(1, id);

        rs = ps.executeQuery();

        while (rs.next()) {
            donators = new Donators(rs.getString("name"), rs.getString("address"),
                    rs.getString("email_id"), rs.getString("mobile_no"), rs.getDouble("amount"),
                    rs.getString("mode_of_payment"), rs.getDate("date").toLocalDate(),
                    rs.getString("aadhar_no"), rs.getString("remark"));
            donators.setReceipt_no(id);
        }

        return donators;
    }

    public static void deleteDonator(String id) throws SQLException{
        String deleteQuery = "DELETE FROM donators_details WHERE ID = ?";

        PreparedStatement ps = getConnection().prepareStatement(deleteQuery);
        ps.setInt(1, Integer.parseInt(id));

        ps.execute();
    }
}

