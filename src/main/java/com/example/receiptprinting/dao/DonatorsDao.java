package com.example.receiptprinting.dao;

import com.example.receiptprinting.models.Donators;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.receiptprinting.utils.DatabaseConnectionManager.getConnection;

public class DonatorsDao {
    public void insertDonators(Donators donators) throws SQLException {

        Connection conn = getConnection();

        String insertSQL = "INSERT INTO donators_details (id, name, address, email_id, mobile_no, amount, mode_of_payment, " +
                "payment_details, date, aadhar_no, remark) VALUES (NEXTVAL('public.receipt_no'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String remark = (donators.getRemark() == null || donators.getRemark().isEmpty()) ? "Voluntary Donation" : donators.getRemark();

        PreparedStatement stmt = conn.prepareStatement(insertSQL);

        stmt.setString(1, donators.getName());
        stmt.setString(2, donators.getAddress());
        stmt.setString(3, donators.getEmail_id());
        stmt.setString(4, donators.getMobile_no());
        stmt.setDouble(5, donators.getAmount());
        stmt.setString(6, donators.getMode_of_payment());
        stmt.setString(7, donators.getPayment_details());
        stmt.setDate(8, Date.valueOf(donators.getDate()));
        stmt.setString(9, donators.getAadhar_no());
        stmt.setString(10, remark);

        stmt.executeUpdate();
    }

    public void update(Donators donators, int id) throws SQLException {

        String updateQuery = "UPDATE DONATORS_DETAILS SET NAME = ?, ADDRESS = ?, EMAIL_ID = ?, MOBILE_NO = ?, AMOUNT = ?," +
                "MODE_OF_PAYMENT = ?, PAYMENT_DETAILS = ?, DATE = ?, AADHAR_NO = ?, REMARK = ? WHERE ID = ?";

        PreparedStatement ps = getConnection().prepareStatement(updateQuery);

        ps.setString(1, donators.getName());
        ps.setString(2, donators.getAddress());
        ps.setString(3, donators.getEmail_id());
        ps.setString(4, donators.getMobile_no());
        ps.setDouble(5, donators.getAmount());
        ps.setString(6, donators.getMode_of_payment());
        ps.setString(7, donators.getPayment_details());
        ps.setDate(8, Date.valueOf(donators.getDate()));
        ps.setString(9, donators.getAadhar_no());
        ps.setString(10, donators.getRemark());
        ps.setInt(11, id);

        ps.executeUpdate();

    }
    public Donators getDonatorsById(int id) throws SQLException {
        String getRecordQuery = "SELECT * FROM DONATORS_DETAILS WHERE ID = ?";
        ResultSet rs;
        Donators donators = null;

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);
        ps.setInt(1, id);

        rs = ps.executeQuery();

        while (rs.next()) {
            donators = mapRowToDonator(rs);
            donators.setReceipt_no(id);
        }

        return donators;
    }

    public void deleteDonator(String id) throws SQLException{
        String deleteQuery = "DELETE FROM donators_details WHERE ID = ?";

        PreparedStatement ps = getConnection().prepareStatement(deleteQuery);
        ps.setInt(1, Integer.parseInt(id));

        ps.execute();
    }

    public List<Donators> getDonatorsByDateRange(LocalDate fromDate, LocalDate toDate) throws SQLException {
        List<Donators> donatorsList = new ArrayList<>(); Donators donators;
        String getRecordQuery = "SELECT * FROM DONATORS_DETAILS WHERE DATE BETWEEN ? AND ?";

        PreparedStatement ps = getConnection().prepareStatement(getRecordQuery);

        ps.setDate(1, Date.valueOf(fromDate));
        ps.setDate(2, Date.valueOf(toDate));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            donators = mapRowToDonator(rs);
            donators.setReceipt_no(rs.getInt("id"));
            donatorsList.add(donators);
        }

        return donatorsList;
    }

    private Donators mapRowToDonator(ResultSet rs) throws SQLException {
        return new Donators(
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("email_id"),
                rs.getString("mobile_no"),
                rs.getDouble("amount"),
                rs.getString("mode_of_payment"),
                rs.getString("payment_details"),
                rs.getDate("date").toLocalDate(),
                rs.getString("aadhar_no"),
                rs.getString("remark")
        );
    }
}
