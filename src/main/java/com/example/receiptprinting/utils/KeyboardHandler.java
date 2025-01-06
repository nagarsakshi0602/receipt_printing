package com.example.receiptprinting.utils;

import com.example.receiptprinting.controllers.EnterDetailsController;
import com.example.receiptprinting.controllers.MainController;
import com.example.receiptprinting.controllers.ReportController;
import com.example.receiptprinting.models.ModeOfPayment;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class KeyboardHandler {
    EnterDetailsController enterDetailsController;
    ReportController reportController;
    MainController mainController;

    public KeyboardHandler(Object controller) {
        if(controller instanceof MainController){
            mainController = (MainController) controller;
        } else if(controller instanceof EnterDetailsController){
            enterDetailsController = (EnterDetailsController) controller;
        } else if( controller instanceof ReportController){
            reportController = (ReportController) controller;
        }
    }

    public void setEnterKeyNavigation(List<Node> fields) {

        fields.forEach(field -> field.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                focusNextField(field, fields);
            }
        }));
    }

    private void focusNextField(Node currentField, List<Node> fields) {
        int currentIndex = fields.indexOf(currentField);
        for (int i = currentIndex + 1; i < fields.size(); i++) {
            Node nextField = fields.get(i);
            if (!nextField.isDisabled()) {
                nextField.requestFocus();
                return;
            }
        }
    }

    public void callSaveOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                enterDetailsController.saveDonator();
            }
        });
    }

    public void callNewOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                enterDetailsController.newDonator();
            }
        });
    }

    public void callUpdateOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                enterDetailsController.updateDonatorDetail();
            }
        });
    }

    public void callFindOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                enterDetailsController.findDonatorDetail();
            }
        });
    }

    public void callGenerateReceiptOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                enterDetailsController.generateReceipt();
            }
        });
    }

    public void callDeleteOnEnter(Button button) {
        // Adding a key press event handler to the button
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Call the method when Enter is pressed
                enterDetailsController.deleteDonator();
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
