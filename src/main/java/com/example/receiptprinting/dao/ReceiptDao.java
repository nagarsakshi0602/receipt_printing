package com.example.receiptprinting.dao;

import com.example.receiptprinting.models.ReceiptSummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.example.receiptprinting.utils.DatabaseConnectionManager.getConnection;

public class ReceiptDao {

    public ObservableList<ReceiptSummary> getAllDonatorsSummary(LocalDate fromDate, LocalDate toDate) throws SQLException {
        ObservableList<ReceiptSummary> summaries = FXCollections.observableArrayList();

        String getRecordQuery = "SELECT DATE, MIN(ID) AS RECEIPT_FROM, MAX(ID) AS RECEIPT_TO, COUNT(ID) AS TOTAL_DONATIONS, SUM(AMOUNT) AS TOTAL_AMOUNT, MODE_OF_PAYMENT" +
                " FROM DONATORS_DETAILS WHERE DATE BETWEEN ? AND ? GROUP BY DATE, MODE_OF_PAYMENT";

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);

        ps.setDate(1, Date.valueOf(fromDate));
        ps.setDate(2, Date.valueOf(toDate));

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            ReceiptSummary receiptSummary = new ReceiptSummary(rs.getDate("DATE").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    rs.getInt("RECEIPT_FROM"), rs.getInt("RECEIPT_TO"), rs.getInt("TOTAL_DONATIONS"),
                    rs.getDouble("TOTAL_AMOUNT"), rs.getString("mode_of_payment"));
            receiptSummary.setDeleted_receipt((receiptSummary.getEnding_receipt_no() - receiptSummary.getStarting_receipt_no() + 1) - rs.getInt("TOTAL_DONATIONS"));
            summaries.add(receiptSummary);

        }
        return summaries;
    }

    public ObservableList<ReceiptSummary> getDonatorsSummaryByPaymentMode(LocalDate fromDate, LocalDate toDate, String modeOfPayment) throws SQLException {
        ObservableList<ReceiptSummary> summaries = FXCollections.observableArrayList();

        String getRecordQuery = "SELECT DATE, MIN(ID) AS RECEIPT_FROM, MAX(ID) AS RECEIPT_TO, COUNT(ID) AS TOTAL_DONATIONS, SUM(AMOUNT) AS TOTAL_AMOUNT, MODE_OF_PAYMENT" +
                " FROM DONATORS_DETAILS WHERE DATE BETWEEN ? AND ? AND MODE_OF_PAYMENT = ? GROUP BY DATE";

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);

        ps.setDate(1, Date.valueOf(fromDate));
        ps.setDate(2, Date.valueOf(toDate));
        ps.setString(3,modeOfPayment.toUpperCase());

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            ReceiptSummary receiptSummary = new ReceiptSummary(rs.getDate("DATE").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    rs.getInt("RECEIPT_FROM"), rs.getInt("RECEIPT_TO"), rs.getInt("TOTAL_DONATIONS"),
                    rs.getDouble("TOTAL_AMOUNT"), modeOfPayment);
            receiptSummary.setDeleted_receipt((receiptSummary.getEnding_receipt_no() - receiptSummary.getStarting_receipt_no() + 1) - rs.getInt("TOTAL_DONATIONS"));
            summaries.add(receiptSummary);

        }

        return summaries;
    }

    public Map<String, Double> getTotalAmountByPaymentMode(LocalDate fromDate, LocalDate toDate) throws SQLException {
        Map<String, Double> paymentSummaryMap = new HashMap<>();
        String getRecordQuery = "SELECT SUM(AMOUNT) AS TOTAL_AMOUNT, MODE_OF_PAYMENT" +
                " FROM DONATORS_DETAILS WHERE DATE BETWEEN ? AND ? GROUP BY MODE_OF_PAYMENT";

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);

        ps.setDate(1, Date.valueOf(fromDate));
        ps.setDate(2, Date.valueOf(toDate));

        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            paymentSummaryMap.put(rs.getString("MODE_OF_PAYMENT"), rs.getDouble("TOTAL_AMOUNT"));
        }

        return paymentSummaryMap;
    }

    public int getLastReceiptId() {

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

}
