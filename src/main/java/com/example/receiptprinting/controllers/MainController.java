package com.example.receiptprinting.controllers;

import com.example.receiptprinting.utils.DatabaseUtil;
import com.example.receiptprinting.utils.PropertyFileLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {

    @FXML
    EnterDetailsController enterDetailsController;
    @FXML
    ReportController reportController;
    @FXML
    SummaryController summaryController;
    @FXML
    Label company_name, company_address, more_info;
    private PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();

    @FXML
    public void initialize() {
        System.out.println("Into intialization phase");
        DatabaseUtil.initializeDatabase();

        try {
            propertyFileLoader.loadProperty("config");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        company_name.setText(propertyFileLoader.getProperty("company_name"));
        company_address.setText(propertyFileLoader.getProperty("company_address"));
        more_info.setText(propertyFileLoader.getProperty("form.more_info"));
    }
}
