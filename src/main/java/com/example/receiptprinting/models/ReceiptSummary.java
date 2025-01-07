package com.example.receiptprinting.models;

public class ReceiptSummary {

    private Integer starting_receipt_no;
    private Integer ending_receipt_no;
    private Double total_amount;
    private String date;
    private Integer total_donations;

    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    private String mode_of_payment;


    private Integer deleted_receipt;

    public ReceiptSummary(String date, Integer starting_receipt_no, Integer ending_receipt_no, Integer total_donations,
                          Double total_amount, String mode_of_payment) {
        this.starting_receipt_no = starting_receipt_no;
        this.ending_receipt_no = ending_receipt_no;
        this.total_amount = total_amount;
        this.date = date;
        this.total_donations = total_donations;
        this.mode_of_payment =mode_of_payment;
    }

    public Integer getStarting_receipt_no() {
        return starting_receipt_no;
    }

    public void setStarting_receipt_no(Integer starting_receipt_no) {
        this.starting_receipt_no = starting_receipt_no;
    }

    public Integer getEnding_receipt_no() {
        return ending_receipt_no;
    }

    public void setEnding_receipt_no(Integer ending_receipt_no) {
        this.ending_receipt_no = ending_receipt_no;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotal_donations() {
        return total_donations;
    }

    public void setTotal_donations(Integer total_donations) {
        this.total_donations = total_donations;
    }

    public Integer getDeleted_receipt() {
        return deleted_receipt;
    }

    public void setDeleted_receipt(Integer deleted_receipt) {
        this.deleted_receipt = deleted_receipt;
    }
}
