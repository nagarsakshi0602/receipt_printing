package com.example.receiptprinting.utils;

import com.example.receiptprinting.models.Donators;
import com.example.receiptprinting.models.ReceiptSummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
                            id INT PRIMARY KEY,
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

    public static void insertDonators(Donators donators) throws SQLException {

        Connection conn = DatabaseUtil.getConnection();

        String insertSQL = "INSERT INTO donators_details (id, name, address, email_id, mobile_no, amount, mode_of_payment, " +
                "date, aadhar_no, remark) VALUES (NEXTVAL('public.receipt_no'), ?, ?, ?, ?, ?, ?, ?, ?, ?)";


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
        //String getSqlQuery = "SELECT TOP 1 ID FROM donators_details ORDER BY ID DESC";
        //String getSqlQuery = "SELECT NEXTVAL('public.receipt_no') AS next_value";

        String getSqlQuery = "SELECT BASE_VALUE FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_NAME = 'RECEIPT_NO';";

        ResultSet resultSet;
        try {
            resultSet = getConnection().createStatement().executeQuery(getSqlQuery);
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("BASE_VALUE"));
                return resultSet.getInt("BASE_VALUE");
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
    public static Donators getDonatorsReport(int id) throws SQLException {
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

    public static ResultSet getDonatorsReport(LocalDate fromDate, LocalDate toDate) throws SQLException {

        String getRecordQuery = "SELECT * FROM DONATORS_DETAILS WHERE DATE BETWEEN ? AND ?";

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);

        ps.setDate(1, Date.valueOf(fromDate));
        ps.setDate(2, Date.valueOf(toDate));

        ResultSet rs = ps.executeQuery();

        return rs;
    }

    public static ObservableList<ReceiptSummary> getDonatorsSummary(LocalDate fromDate, LocalDate toDate) throws SQLException {
        ObservableList<ReceiptSummary> summaries = FXCollections.observableArrayList();

        String getRecordQuery = "SELECT DATE, MIN(ID) AS RECEIPT_FROM, MAX(ID) AS RECEIPT_TO, COUNT(ID) AS TOTAL_DONATIONS, SUM(AMOUNT) AS TOTAL_AMOUNT" +
                " FROM DONATORS_DETAILS WHERE DATE BETWEEN ? AND ? GROUP BY DATE";

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);

        ps.setDate(1, Date.valueOf(fromDate));
        ps.setDate(2, Date.valueOf(toDate));

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            ReceiptSummary receiptSummary = new ReceiptSummary(rs.getDate("DATE").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    rs.getInt("RECEIPT_FROM"), rs.getInt("RECEIPT_TO"), rs.getInt("TOTAL_DONATIONS"),
                    rs.getDouble("TOTAL_AMOUNT"));
            receiptSummary.setDeleted_receipt((receiptSummary.getEnding_receipt_no() - receiptSummary.getStarting_receipt_no() + 1) - rs.getInt("TOTAL_DONATIONS"));
            summaries.add(receiptSummary);

        }

        return summaries;
    }
}

