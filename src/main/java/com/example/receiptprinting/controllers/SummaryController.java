package com.example.receiptprinting.controllers;

import com.example.receiptprinting.models.ModeOfPayment;
import com.example.receiptprinting.models.ReceiptSummary;
import com.example.receiptprinting.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.example.receiptprinting.utils.CommonUtils.formatDate;

public class SummaryController {

    @FXML
    DatePicker from_date, to_date;

    @FXML
    TableView<ReceiptSummary> summary_table;

    @FXML
    ComboBox mode_of_payment;

    @FXML
    public void initialize() {
        from_date.requestFocus();
        initializeTableColumns();
        mode_of_payment.setItems(FXCollections.observableArrayList(ModeOfPayment.values()));
        mode_of_payment.getItems().add("ALL");
        mode_of_payment.setValue("ALL"); // Default value
        ValidationListeners validationListeners = new ValidationListeners();
        validationListeners.validateDate(from_date);
        validationListeners.validateDate(to_date);
        CommonUtils.dateConverter(from_date);
        CommonUtils.dateConverter(to_date);
        from_date.setValue(LocalDate.now());
        to_date.setValue(LocalDate.now());
        //setupPagination();
        CommonUtils.resizeColumn(summary_table);
    }

    @FXML
    public void getSummary() throws SQLException {

        if(mode_of_payment.getValue() == null || mode_of_payment.getValue().toString().equalsIgnoreCase("All")) {
            try {
                ObservableList<ReceiptSummary> donatorsSummary = DatabaseUtil.getAllDonatorsSummary(from_date.getValue(), to_date.getValue());
                summary_table.setItems(donatorsSummary);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            ObservableList<ReceiptSummary> donatorsSummaryByMode = DatabaseUtil.getDonatorsSummaryByMode(from_date.getValue(),
                    to_date.getValue(), mode_of_payment.getValue().toString());
            summary_table.setItems(donatorsSummaryByMode);
        }

    }

    private void initializeTableColumns() {
        TableColumn<ReceiptSummary, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<ReceiptSummary, Integer> startReceiptColumn = new TableColumn<>("From Receipt No");
        startReceiptColumn.setCellValueFactory(new PropertyValueFactory<>("starting_receipt_no"));

        TableColumn<ReceiptSummary, Integer> endReceiptColumn = new TableColumn<>("To Receipt No");
        endReceiptColumn.setCellValueFactory(new PropertyValueFactory<>("ending_receipt_no"));

        TableColumn<ReceiptSummary, String> modeOfPaymentColumn = new TableColumn<>("Mode of Payment");
        modeOfPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("mode_of_payment"));

        TableColumn<ReceiptSummary, Integer> totalDonationsColumn = new TableColumn<>("Total Receipts");
        totalDonationsColumn.setCellValueFactory(new PropertyValueFactory<>("total_donations"));

        TableColumn<ReceiptSummary, Integer> deletedDonationColumn = new TableColumn<>("Deleted Receipts");
        deletedDonationColumn.setCellValueFactory(new PropertyValueFactory<>("deleted_receipt"));

        TableColumn<ReceiptSummary, Double> totalAmountColumn = new TableColumn<>("Total Amount");
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("total_amount"));
        totalAmountColumn.setCellFactory(new Callback<TableColumn<ReceiptSummary, Double>, TableCell<ReceiptSummary, Double>>() {

            @Override
            public TableCell<ReceiptSummary, Double> call(TableColumn<ReceiptSummary, Double> donatorsDoubleTableColumn) {
                return new TableCell<ReceiptSummary, Double>() {
                    @Override
                    protected void updateItem(Double price, boolean empty) {
                        super.updateItem(price, empty);
                        if (empty || price == null) {
                            setText(null);
                        } else {
                            setText(String.format("%.2f", price)); // Format price
                        }
                        setStyle("-fx-alignment: CENTER-RIGHT;"); // Align text
                    }
                };
            }
        });

        summary_table.getColumns().addAll(dateColumn, startReceiptColumn, endReceiptColumn, modeOfPaymentColumn, totalDonationsColumn, deletedDonationColumn, totalAmountColumn);
    }

    @FXML
    public void printSummary() {
        JasperReportUtil.generateSummary(summary_table, formatDate(from_date.getValue()), formatDate(to_date.getValue()));
    }

    @FXML
    public void exportSummary() throws IOException {
        new ExcelFileHandler().openFileSelectionWindow(summary_table, "Summary", "Receipt Summary for the Period: " +
                formatDate(from_date.getValue()) + " To: " + formatDate(to_date.getValue()));
    }
}
