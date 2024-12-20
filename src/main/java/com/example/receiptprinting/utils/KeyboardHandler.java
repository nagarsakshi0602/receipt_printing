package com.example.receiptprinting.utils;

import com.example.receiptprinting.controllers.FormController;
import com.example.receiptprinting.models.ModeOfPayment;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class KeyboardHandler {
    FormController formController;

    public KeyboardHandler(FormController formController) {
        this.formController = formController;
    }

    public void setEnterKeyNavigation(List<Node> fields) {
        for (int i = 0; i < fields.size(); i++) {
            Node currentField = fields.get(i);
            Node nextField = (i + 1 < fields.size()) ? fields.get(i + 1) : null;

            // Set Enter key action for current field
            currentField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (nextField != null) {
                        nextField.requestFocus();  // Move focus to next field
                    }
                }
            });
        }
    }

    public void callSaveOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                formController.saveDonator();
            }
        });
    }

    public void callNewOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                formController.newDonator();
            }
        });
    }

    public void callUpdateOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                formController.updateDonatorDetail();
            }
        });
    }

    public void callFindOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                formController.findDonatorDetail();
            }
        });
    }

    public void callGenerateReceiptOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                formController.generateReceipt();
            }
        });
    }

    public void callDeleteOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                formController.deleteDonator();
            }
        });
    }

    public void setButtonNavigation(List<Button> fields) {
        for (int i = 0; i < fields.size(); i++) {
            Node currentField = fields.get(i);
            Node nextField = (i + 1 < fields.size()) ? fields.get(i + 1) : null;

            // Set Enter key action for current field
            currentField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.TAB) {
                    if (nextField != null) {
                        nextField.requestFocus();  // Move focus to next field
                    }
                }
            });
        }
    }

    public void selectOptionOnKeyPress(ChoiceBox choiceBox) {
        // Adding a key press event handler to the choice box
        choiceBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->{
            if (choiceBox.isFocused()) {
                if (event.getCode() == KeyCode.C) {
                    choiceBox.setValue(ModeOfPayment.CASH);
                } else if (event.getCode() == KeyCode.Q) {
                    choiceBox.setValue(ModeOfPayment.CHEQUE);
                } else if (event.getCode() == KeyCode.O) {
                    choiceBox.setValue(ModeOfPayment.ONLINE_TRANSFER);
                }
            }
        });
    }
}
