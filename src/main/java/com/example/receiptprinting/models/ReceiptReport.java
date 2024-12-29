package com.example.receiptprinting.models;

public class ReceiptReport {

    private Integer receipt_no;
    private String name;
    private Double amount;
    private String date;
    private String mode_of_payment;
    private String mobile_no;


    public ReceiptReport(Integer receipt_no, String name, Double amount, String date, String mode_of_payment, String mobile_no) {
        this.receipt_no = receipt_no;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.mode_of_payment = mode_of_payment;
        this.mobile_no = mobile_no;
    }

    public Integer getReceipt_no() {
        return receipt_no;
    }

    public void setReceipt_no(Integer receipt_no) {
        this.receipt_no = receipt_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

}