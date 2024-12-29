package com.example.receiptprinting.controllers;

import com.example.receiptprinting.models.Donators;
import com.example.receiptprinting.models.ModeOfPayment;
import com.example.receiptprinting.utils.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnterDetailsController{

    @FXML
    private TextField receipt_no, name, address, mobile_no, email_id, amount, aadhar_no, remark;
    @FXML
    private ChoiceBox mode_of_payment;
    @FXML
    private DatePicker date;
    @FXML
    private Button save, reset, find, update, receipt, delete;
    @FXML
    Label errorLabel;

    public static Donators donators;
    public static String receiptNew;
    private final ValidationListeners validationListeners = new ValidationListeners();
    String isAdvancedValidation;
    private DateTimeFormatter dateFormatter;


    @FXML
    public void initialize() {
        save.setDisable(true);
        Platform.runLater(() -> reset.requestFocus());
        mode_of_payment.setItems(FXCollections.observableArrayList(ModeOfPayment.values()));
        dateFormatter = CommonUtils.dateConverter(date);

        isAdvancedValidation = PropertyFileLoader.getInstance().getProperty("advanced_validation");
        initializeKeyboardHandlers();
        addValidationListeners();
    }

    @FXML
    public void newDonator() {
        clearFields();
        if(DatabaseUtil.getLastReceiptId() == 0){
            receiptNew = PropertyFileLoader.getInstance().getProperty("starting_id");
        }
        else{
            receiptNew = String.valueOf( DatabaseUtil.getLastReceiptId());
        }

        receipt_no.setDisable(true);
        save.setDisable(false);
        receipt_no.setText(receiptNew);
        date.requestFocus();
        date.setValue(LocalDate.now());
    }

    @FXML
    public void saveDonator() {
        if (isAdvancedValidation.equalsIgnoreCase("false") || isAdvancedValidation.equalsIgnoreCase("no")) {
            System.out.println("Into basic validation");
            if (isRequiredFieldPresent()) {
                save();

            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment and Date. " +
                        "Please enter everything before saving", errorLabel);
            }
        } else {
            if (isRequiredFieldPresent() && isAdvancedValidationFieldsPresent()) {
                System.out.println("Into Advanced Validation");
                save();
            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment, Date and Either Mobile or Aadhar details. " +
                        "Please enter everything before saving", errorLabel);
            }
        }
    }

    @FXML
    public void updateDonatorDetail() {
        if (!save.isDisabled()) {
            save.setDisable(true);
        }
        if (isAdvancedValidation.equalsIgnoreCase("false") || isAdvancedValidation.equalsIgnoreCase("no")) {
            if (isRequiredFieldPresent()) {
                update();

            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment and Date. " +
                        "Please enter everything before saving", errorLabel);
            }
        } else {
            if (isRequiredFieldPresent() && isAdvancedValidationFieldsPresent()) {
                update();
            } else {
                CommonUtils.showError("Required : Name, Amount, Mode of Payment, Date and Either Mobile or Aadhar details. " +
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
            donators = DatabaseUtil.getDonatorsReport(id);
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
            Donators donators = DatabaseUtil.getDonatorsReport(id);
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

        keyboardHandler.setEnterKeyNavigation(Arrays.asList(receipt_no, date, name, amount,
                mode_of_payment, mobile_no, aadhar_no, address, email_id, remark, save));
        keyboardHandler.setButtonNavigation(Arrays.asList(reset, save, update, receipt, find, delete));
        keyboardHandler.callNewOnEnter(reset);
        keyboardHandler.callSaveOnEnter(save);
        keyboardHandler.callUpdateOnEnter(update);
        keyboardHandler.callGenerateReceiptOnEnter(receipt);
        keyboardHandler.callFindOnEnter(find);
        keyboardHandler.callDeleteOnEnter(delete);
        keyboardHandler.selectOptionOnKeyPress(mode_of_payment);


    }

    private void addValidationListeners() {
        validationListeners.restrictToNumber(mobile_no);
        validationListeners.restrictToDecimalNumber(amount);
        validationListeners.restrictToNumber(aadhar_no);
        validationListeners.restrictToMaxLength(aadhar_no, 12);
        validationListeners.restrictToMaxLength(mobile_no, 15);
        validationListeners.restrictToAlphabetsAndSpaces(name);
        validationListeners.validateDate(date);

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

        if (isFormValid()) {
            CommonUtils.showError("", errorLabel);
            return;
        }
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
