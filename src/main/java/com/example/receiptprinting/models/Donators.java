package com.example.receiptprinting.models;

import java.time.LocalDate;

public class Donators {

    private int receipt_no;
    private String name;
    private String address;
    private String email_id;
    private String mobile_no;
    private Double amount;
    private String mode_of_payment;
    private LocalDate date;
    private String aadhar_no;

    private String remark;

    public Donators(String name, String address, String email_id, String mobile_no, Double amount,
                    String mode_of_payment, LocalDate date, String aadhar_no, String remark) {

        this.name = name;
        this.address = address;
        this.email_id = email_id;
        this.mobile_no = mobile_no;
        this.amount = amount;
        this.mode_of_payment = mode_of_payment;
        this.date = date;
        this.aadhar_no = aadhar_no;
        this.remark = remark;
    }

    public int getReceipt_no() {
        return receipt_no;
    }

    public void setReceipt_no(int receipt_no) {
        this.receipt_no = receipt_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
