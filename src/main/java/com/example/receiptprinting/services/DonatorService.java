package com.example.receiptprinting.services;

import com.example.receiptprinting.dao.DonatorsDao;
import com.example.receiptprinting.dao.ReceiptDao;
import com.example.receiptprinting.models.Donators;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DonatorService {
    private final DonatorsDao donatorsDao;
    private final ReceiptDao receiptDao;

    public DonatorService() {
        this.donatorsDao = new DonatorsDao();
        this.receiptDao = new ReceiptDao();
    }

    public int getLastReceiptId() throws SQLException {
        return receiptDao.getLastReceiptId();
    }

    public Donators getDonatorById(int id) throws SQLException {
        return donatorsDao.getDonatorsById(id);
    }

    public void saveDonator(Donators donator) throws SQLException {
        donatorsDao.insertDonators(donator);
    }

    public void updateDonator(Donators donator, int receiptId) throws SQLException {
        donatorsDao.update(donator, receiptId);
    }

    public void deleteDonator(int receiptId) throws SQLException {
        donatorsDao.deleteDonator(String.valueOf(receiptId));
    }

    public List<Donators> getDonatorByDateRange(LocalDate fromDate, LocalDate toDate) throws SQLException {
        return donatorsDao.getDonatorsByDateRange(fromDate,toDate);
    }
}
