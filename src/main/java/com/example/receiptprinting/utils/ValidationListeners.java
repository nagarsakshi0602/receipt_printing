package com.example.receiptprinting.utils;

import com.example.receiptprinting.models.ModeOfPayment;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;


public class ValidationListeners {
    private final Map<Object, Boolean> fieldValidityMap = new HashMap<>();

    public void restrictToNumber(TextField textField) {

        // Add validation to restrict input to numbers only
        textField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", "")); // Remove non-numeric characters
            }
        });
    }

    public void restrictToDecimalNumber(TextField textField) {

        // Add validation to restrict input to numbers only
        textField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                textField.setText(newValue.replaceAll("[^\\d.]", "")); // Remove non-numeric characters
            }
        });
    }

    public void restrictToAlphabetsAndSpaces(TextField nameField) {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]+")) {
                nameField.setText(newValue.replaceAll("[^a-zA-Z\\s]", "")); // Remove non-alphabetic characters
            }
        });
    }

    public void restrictToCapitalsAndNumbers(TextField textField){
        // Create a TextFormatter to convert lowercase to uppercase and allow numbers
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String input = change.getText();
            // Convert lowercase letters to uppercase
            if (input.matches("[a-z0-9]*")) {
                change.setText(input.toUpperCase());
                return change;
            }
            // Reject any other input
            if (!input.isEmpty()) {
                return null;
            }
            return change;
        };
        // Apply the TextFormatter to the TextField
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);

    }

    public boolean isEmailValid(TextField emailField) {
       return   emailField.getText().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean isPhoneNumberValid(TextField phoneField) {
        return  phoneField.getText().matches("\\d{10,15}");
    }

    public boolean isAadharValid(TextField aadharField) {
       return aadharField.getText().matches("\\d{12}");
    }

    // Method to restrict TextField to a maximum length
    public void restrictToMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue); // Revert to old value if it exceeds max length
            }
        });
    }

    // Validate non-empty text
    public boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public void validateDate(DatePicker datePicker) {

        // Desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Listen to textProperty of the DatePicker's editor
        datePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            // Avoid infinite loops by not processing if the value didn't change
            if (newValue.equals(oldValue)) {
                return;
            }

            // Remove non-digit characters
            String digits = newValue.replaceAll("[^0-9]", "");
            StringBuilder formatted = new StringBuilder();

            // Format input dynamically as "dd-MM-yyyy"
            if (digits.length() > 0) {
                formatted.append(digits.substring(0, Math.min(2, digits.length()))); // dd
            }
            if (digits.length() > 2) {
                formatted.append("-").append(digits.substring(2, Math.min(4, digits.length()))); // MM
            }
            if (digits.length() > 4) {
                formatted.append("-").append(digits.substring(4, Math.min(8, digits.length()))); // yyyy
            }

            // Update the editor's text while preserving caret position
            int caretPosition = datePicker.getEditor().getCaretPosition();
            String result = formatted.toString();
            datePicker.getEditor().setText(result);

            // Prevent the caret position from exceeding the text length
            if (caretPosition > result.length()) {
                caretPosition = result.length();
            }
            datePicker.getEditor().positionCaret(caretPosition);
        });

        // Validate the input on focus loss
        datePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // On focus loss
                String text = datePicker.getEditor().getText();
                try {
                    LocalDate date = LocalDate.parse(text, formatter); // Parse and validate
                    datePicker.setValue(date); // Set the DatePicker value if valid
                } catch (DateTimeParseException e) {
                    datePicker.getEditor().setText(""); // Clear invalid input
                }
            }
        });
    }

    public void enablePaymentDetails(ChoiceBox choiceBox, TextField paymentDetails){
        choiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(newValue != null){
                        // Enable the TextField only when "Enable Field" is selected
                        if(newValue.equals(ModeOfPayment.CASH)){
                            paymentDetails.setDisable(true);
                        }
                        else{
                            paymentDetails.setDisable(false);
                        }
                    }
                });
    }
}
