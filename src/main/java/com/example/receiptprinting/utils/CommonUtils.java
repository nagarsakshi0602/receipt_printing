package com.example.receiptprinting.utils;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommonUtils {
    static String one[] = {"", "one ", "two ", "three ", "four ",
            "five ", "six ", "seven ", "eight ",
            "nine ", "ten ", "eleven ", "twelve ",
            "thirteen ", "fourteen ", "fifteen ",
            "sixteen ", "seventeen ", "eighteen ",
            "nineteen "};

    // Strings at index 0 and 1 are not used, they are to
    // make array indexing simple
    static String ten[] = {"", "", "twenty ", "thirty ", "forty ",
            "fifty ", "sixty ", "seventy ", "eighty ",
            "ninety "};

    static String amountToWords(int n, String s) {

        // n is 1- or 2-digit number

        String str = "";
        // if n is more than 19, divide it
        if (n > 19) {
            str += ten[n / 10] + one[n % 10];
        } else {
            str += one[n];
        }

        // if n is non-zero
        if (n != 0) {
            str += s;
        }

        return str;
    }

    // Function to print a given number in words
    public static String convertToWords(Double amount) {
        /*int n = amount.intValue();
        // stores word representation of given number n
        String out = "";

        // handles digits at ten millions and hundred
        // millions places (if any)
        out += amountToWords((int) (n / 10000000), "crore ");

        // handles digits at hundred thousands and one
        // millions places (if any)
        out += amountToWords((int) ((n / 100000) % 100), "lakh ");

        // handles digits at thousands and tens thousands
        // places (if any)
        out += amountToWords((int) ((n / 1000) % 100), "thousand ");

        // handles digit at hundreds places (if any)
        out += amountToWords((int) ((n / 100) % 10), "hundred ");

        if (n > 100 && n % 100 > 0) {
            out += "and ";
        }

        // handles digits at ones and tens places (if any)
        out += amountToWords((int) (n % 100), "");

        return out;*/


        BigDecimal bd = BigDecimal.valueOf(amount);
        bd.setScale(2, RoundingMode.HALF_UP);
        long number = bd.longValue();
        long no = bd.longValue();
        //int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
        int decimal = bd.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).intValue();

        int digits_length = String.valueOf(no).length();
        int i = 0;
        ArrayList<String> str = new ArrayList<>();
        HashMap<Integer, String> words = new HashMap<>();
        words.put(0, "");
        words.put(1, "One");
        words.put(2, "Two");
        words.put(3, "Three");
        words.put(4, "Four");
        words.put(5, "Five");
        words.put(6, "Six");
        words.put(7, "Seven");
        words.put(8, "Eight");
        words.put(9, "Nine");
        words.put(10, "Ten");
        words.put(11, "Eleven");
        words.put(12, "Twelve");
        words.put(13, "Thirteen");
        words.put(14, "Fourteen");
        words.put(15, "Fifteen");
        words.put(16, "Sixteen");
        words.put(17, "Seventeen");
        words.put(18, "Eighteen");
        words.put(19, "Nineteen");
        words.put(20, "Twenty");
        words.put(30, "Thirty");
        words.put(40, "Forty");
        words.put(50, "Fifty");
        words.put(60, "Sixty");
        words.put(70, "Seventy");
        words.put(80, "Eighty");
        words.put(90, "Ninety");
        String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore"};
        while (i < digits_length) {
            int divider = (i == 2) ? 10 : 100;
            number = no % divider;
            no = no / divider;
            i += divider == 10 ? 1 : 2;
            if (number > 0) {
                int counter = str.size();
                String plural = (counter > 0 && number > 9) ? "s" : "";
                String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural : words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " " + words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
                str.add(tmp);
            } else {
                str.add("");
            }
        }

        Collections.reverse(str);
        String Rupees = String.join(" ", str).trim();

        System.out.println(decimal);

        String paise = (decimal) > 0 ? " And " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " "
                + words.get(Integer.valueOf((int) (decimal % 10))) + " Paise": "";

        return  toProperCase(Rupees + paise + " Only");

    }

    public static String toProperCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char ch : text.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capitalizeNext = true;
                result.append(ch);
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(ch));
                } else {
                    result.append(Character.toLowerCase(ch));
                }
                capitalizeNext = false;
            }
        }

        return result.toString();
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getDialogPane().getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                alert.close();
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                alert.close();
            }
        });

        alert.showAndWait();
    }

    public static boolean confirmationAlert(String title, String message) {
        AtomicBoolean response = new AtomicBoolean(false);
        AtomicBoolean keyHandled = new AtomicBoolean(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);

        ButtonType yes = ButtonType.YES;
        ButtonType no = ButtonType.NO;
        alert.getButtonTypes().setAll(yes, no);


        alert.getDialogPane().getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (alert.getDialogPane().lookupButton(yes).isFocused()) {
                    response.set(true);
                    alert.close();
                    keyHandled.set(true);
                } else if (alert.getDialogPane().lookupButton(no).isFocused()) {
                    response.set(false);
                    alert.close();
                    keyHandled.set(true);
                }
            }

        });

        Optional<ButtonType> res = alert.showAndWait();
        if (keyHandled.get()) {
            return response.get();
        }
        if (res.isPresent()) {
            if (res.get() == yes) {
                response.set(true);
                alert.close();
            } else if (res.get() == no) {
                response.set(false);
                alert.close();
            }
        }

        return response.get();
    }

    public static void showError(String message, Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setVisible(true);
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public static void appendError(String message, Label errorLabel) {
        errorLabel.setText(errorLabel.getText() + " " + message);
        errorLabel.setStyle("-fx-text-fill: red;");

    }
    public static String formatDate(LocalDate from_date) {
        return from_date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static DateTimeFormatter dateConverter(DatePicker datePicker) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Set a custom StringConverter to display the date in the desired format
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                // Format LocalDate to String
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                // Parse String to LocalDate
                try {
                    return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });

        // Listener to get the selected date in the desired format
        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                String formattedDate = selectedDate.format(dateFormatter);
            }
        });
        return dateFormatter;
    }


    public static <T> void resizeColumn(TableView<T> tableView) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableColumn<T, ?> column : tableView.getColumns()) {
            column.setPrefWidth(1); // Reset to auto-resize
        }
    }
}