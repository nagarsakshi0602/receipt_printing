package com.example.receiptprinting.controllers;

import com.example.receiptprinting.models.Donators;
import com.example.receiptprinting.utils.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormController {
    @FXML
    private TextField receipt_no, name, address, mobile_no, email_id, amount, aadhar_no, remark;
    @FXML
    private ChoiceBox mode_of_payment;
    @FXML
    private DatePicker date;
    @FXML
    private Button save, reset, find, update, receipt, delete;

    @FXML
    Label errorLabel, companyName, companyAddress, moreInfo;

    public static Donators donators;
    public static String receiptNew;
    private final ValidationListeners validationListeners = new ValidationListeners();
    private PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
    String isAdvancedValidation;

    @FXML
    public void initialize() {
        System.out.println("Into intialization phase");
        DatabaseUtil.initializeDatabase();
        save.setDisable(true);

        try {
            propertyFileLoader.loadProperty("config");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        companyName.setText(propertyFileLoader.getProperty("company_name"));
        companyAddress.setText(propertyFileLoader.getProperty("company_address"));
        moreInfo.setText(propertyFileLoader.getProperty("form.more_info"));
        isAdvancedValidation = propertyFileLoader.getProperty("advanced_validation");
        System.out.println(isAdvancedValidation + " : isAdvancedValidation");

        CommonUtils.dateConverter(date);
        initializeKeyboardHandlers();
        addValidationListeners();
    }
    @FXML
    public void newDonator() {
        clearFields();
        receiptNew = String.valueOf(DatabaseUtil.getLastReceiptId() + 1);
        receipt_no.setDisable(true);
        save.setDisable(false);
        receipt_no.setText(receiptNew);
        date.requestFocus();
    }

    @FXML
    public void saveDonator() {
        if (isAdvancedValidation.equalsIgnoreCase("false") || isAdvancedValidation.equalsIgnoreCase("no")) {
            System.out.print("Into basic validation");
            if (isRequiredFieldPresent()) {
                save();

            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment and Date" +
                        "Please enter everything before saving", errorLabel);
            }
        } else {
            if (isRequiredFieldPresent() && isAdvancedValidationFieldsPresent()) {
                System.out.println("Into Advanced Validation");
                save();
            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment, Date and Either Mobile or Aadhar details" +
                        "Please enter everything before saving", errorLabel);
            }
        }

    }

    @FXML
    public void updateDonatorDetail() {
        // validationListeners.addNameValidation(name, errorLabel);
        if (!save.isDisabled()) {
            save.setDisable(true);
        }
        if (isAdvancedValidation.equalsIgnoreCase("false") || isAdvancedValidation.equalsIgnoreCase("no")) {
            if (isRequiredFieldPresent()) {
                update();

            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment and Date" +
                        "Please enter everything before saving", errorLabel);
            }
        } else {
            if (isRequiredFieldPresent() && isAdvancedValidationFieldsPresent()) {
                update();
            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment, Date and Either Mobile or Aadhar details" +
                        "Please enter everything before saving", errorLabel);
            }
        }
    }

    @FXML
    public void findDonatorDetail() {
        if (receipt_no.isDisabled()) {
            receipt_no.setDisable(false);
            receipt_no.setText(String.valueOf(Integer.parseInt(receipt_no.getText()) - 1));
        }
        save.setDisable(true);

        int id = Integer.parseInt(receipt_no.getText());
        try {
            donators = DatabaseUtil.getDonatorsDetail(id);
            if (donators == null) {
                CommonUtils.showAlert("Record Not Found", "Record with this receipt no. : " + id + " not found");
            } else {
                name.setText(donators.getName());
                address.setText(donators.getAddress());
                mobile_no.setText(donators.getMobile_no());
                email_id.setText(donators.getEmail_id());
                mode_of_payment.setValue(donators.getMode_of_payment());
                amount.setText(donators.getAmount().toString());
                date.setValue(donators.getDate());
                aadhar_no.setText(donators.getAadhar_no());
                remark.setText(donators.getRemark());
            }
        } catch (SQLException e) {
            CommonUtils.showAlert("Database Error", "Unable to connect to database");
        }
    }

    @FXML
    public void generateReceipt() {
        int id = Integer.parseInt(receipt_no.getText());
        List donator_list = new ArrayList<>();
        try {
            Donators donators = DatabaseUtil.getDonatorsDetail(id);
            if (donators == null) {
                CommonUtils.showAlert("Record Error", "Record with this id: " + id + " not found");
            } else {
                donator_list.add(donators);
                JasperReportUtil.generateReport(donator_list);
            }
        } catch (SQLException e) {
            CommonUtils.showAlert("Database Error", "Unable to connect to database");
        }

    }

    @FXML
    public void deleteDonator() {
        String id = receipt_no.getText();
        if (id != null) {
            try {
                DatabaseUtil.deleteDonator(id);
                clearFields();
                receipt_no.setText("");
                CommonUtils.showAlert("Delete Successful", "Record with id: " + id + " is deleted successfully");
            } catch (SQLException e) {
                CommonUtils.showAlert("Database Error", "Unable to connect to database");
            }
        } else {
            CommonUtils.showAlert("Record Error", "Record with id: " + id + " not found");
        }

    }

    private boolean isRequiredFieldPresent() {
        return validationListeners.isNotEmpty(name.getText()) && validationListeners.isNotEmpty(amount.getText())
                && mode_of_payment.getValue() != null && date.getValue() != null;
    }


    private boolean isFormValid() {
        boolean isValid;
        if (!email_id.getText().isEmpty()) {
            isValid = validationListeners.isEmailValid(email_id);
            if (!isValid) {
                CommonUtils.showError("Invalid Email Id. Input valid format: sample@abc.com", errorLabel);
                return true;
            }
        }
        if (!mobile_no.getText().isEmpty()) {
            isValid = validationListeners.isPhoneNumberValid(mobile_no);
            if (!isValid) {
                CommonUtils.showError("Invalid Phone No. Either enter 10 digit No or with country code", errorLabel);
                return true;
            }
        }
        if (!aadhar_no.getText().isEmpty()) {
            isValid = validationListeners.isAadharValid(aadhar_no);
            if (!isValid) {
                CommonUtils.showError("Invalid Aadhar. Aadhar digits cannot be less than 12", errorLabel);
                return true;
            }
        }
        return false;
    }

    private boolean isAdvancedValidationFieldsPresent() {
        return !mobile_no.getText().isEmpty() || !aadhar_no.getText().isEmpty();
    }

    private void initializeKeyboardHandlers() {
        KeyboardHandler keyboardHandler = new KeyboardHandler(this);

        keyboardHandler.setEnterKeyNavigation(Arrays.asList(receipt_no, date, name, address, mobile_no, email_id, amount,
                mode_of_payment, aadhar_no, remark, save));
        keyboardHandler.setButtonNavigation(Arrays.asList(reset, save, update, receipt, find, delete));
        keyboardHandler.callNewOnEnter(reset);
        keyboardHandler.callSaveOnEnter(save);
        keyboardHandler.callUpdateOnEnter(update);
        keyboardHandler.callGenerateReceiptOnEnter(receipt);
        keyboardHandler.callFindOnEnter(find);
        keyboardHandler.callDeleteOnEnter(delete);


    }

    private void addValidationListeners() {
        validationListeners.restrictToNumber(mobile_no);
        validationListeners.restrictToNumber(amount);
        validationListeners.restrictToNumber(aadhar_no);
        validationListeners.restrictToMaxLength(aadhar_no, 12);
        validationListeners.restrictToMaxLength(mobile_no, 15);
        validationListeners.restrictToAlphabetsAndSpaces(name);

    }

    private void clearFields() {
        name.clear();
        email_id.clear();
        address.clear();
        mobile_no.clear();
        amount.clear();
        aadhar_no.clear();
        remark.clear();
        date.setValue(null);
        mode_of_payment.setValue(null);
    }

    private void update() {
        donators = new Donators(name.getText(), address.getText(), email_id.getText(), mobile_no.getText(),
                Double.parseDouble(amount.getText()), mode_of_payment.getValue().toString(), date.getValue(),
                aadhar_no.getText(), remark.getText());
        boolean isValid = true;
        //if (isFormValid()) {
        if (isFormValid()) return;
        try {
            DatabaseUtil.update(donators, Integer.parseInt(receipt_no.getText()));
            if (CommonUtils.confirmationAlert("Print Confirmation", "Do you want to print updated receipt?")) {
                generateReceipt();
            }
            clearFields();
            CommonUtils.showError("", errorLabel);
            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            CommonUtils.showAlert("Database Error", "Could not update data.");
        }
    }

    private void save() {
        donators = new Donators(name.getText(), address.getText(), email_id.getText(), mobile_no.getText(),
                Double.parseDouble(amount.getText()), mode_of_payment.getValue().toString(), date.getValue(),
                aadhar_no.getText(), remark.getText());
        boolean isValid = true;
        //if (isFormValid()) {
        if (isFormValid()) return;
        try {
            DatabaseUtil.insertDonators(donators);
            if (CommonUtils.confirmationAlert("Print Confirmation", "Do you want to print receipt?")) {
                generateReceipt();
            }
            clearFields();
            receipt_no.setDisable(false);
            save.setDisable(true);
            CommonUtils.showError("", errorLabel);
            System.out.println("Record saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            CommonUtils.showAlert("Database Error", "Could not save data.");
        }
    }


}
