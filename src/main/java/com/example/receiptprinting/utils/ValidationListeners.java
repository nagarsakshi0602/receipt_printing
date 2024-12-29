package com.example.receiptprinting.utils;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;


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

  /*  public void addNameValidation(TextField nameField, Label errorLabel) {

        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {

            boolean isValid = !newValue.toString().trim().isEmpty() && newValue.toString().matches("[a-zA-Z\\s]+");
            fieldValidityMap.put(nameField, isValid);

            if (isValid) {
                errorLabel.setText("");
            } else {
                errorLabel.setText("Invalid name. Only letters and spaces are allowed.");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        });
        fieldValidityMap.put(nameField, false);

    }*/

    public boolean isEmailValid(TextField emailField) {
        //AtomicBoolean isValid = new AtomicBoolean(false);
        boolean isValid = emailField.getText().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        //emailField.textProperty().addListener((observable, oldValue, newValue) -> {

        // isValid.set(newValue.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"));
        /*if (isValid) {
            errorLabel.setText("");
        } else {
            errorLabel.setText("Invalid email address. Valid format: example@abc.com");
            errorLabel.setStyle("-fx-text-fill: red;");

        }
        // });*/
        return isValid;

    }

    public boolean isPhoneNumberValid(TextField phoneField) {
        //phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
        boolean isValid = phoneField.getText().matches("\\d{10,15}");
         /*   fieldValidityMap.put(phoneField, isValid);

            if (isValid) {
                errorLabel.setText("");
            } else {
                errorLabel.setText("Invalid phone number. Must be 10 digits or with country code");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        });

        fieldValidityMap.put(phoneField, false);*/
        return isValid;
    }

    public boolean isAadharValid(TextField aadharField) {
        //aadharField.textProperty().addListener((observable, oldValue, newValue) -> {

        boolean isValid = aadharField.getText().matches("\\d{12}");
            /*fieldValidityMap.put(aadharField, isValid);

            if (isValid) {
                errorLabel.setText("");
            } else {
                errorLabel.setText("Invalid Aadhar. Digits are less than 12");
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        });

        fieldValidityMap.put(aadharField, false);*/
        return isValid;
    }

    // Method to restrict TextField to a maximum length
    public void restrictToMaxLength(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength) {
                textField.setText(oldValue); // Revert to old value if it exceeds max length
            }
        });
    }


    // Validate all fields in the form

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
}
