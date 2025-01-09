package com.example.receiptprinting.controllers;

import com.example.receiptprinting.dao.ReceiptDao;
import com.example.receiptprinting.models.Donators;
import com.example.receiptprinting.models.ModeOfPayment;
import com.example.receiptprinting.models.ReceiptReport;
import com.example.receiptprinting.services.DonatorService;
import com.example.receiptprinting.services.JasperReportService;
import com.example.receiptprinting.utils.CommonUtils;
import com.example.receiptprinting.utils.ExcelFileHandler;
import com.example.receiptprinting.utils.ValidationListeners;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.example.receiptprinting.utils.CommonUtils.formatDate;

public class ReportController {

    @FXML
    DatePicker from_date, to_date;
    @FXML
    Pagination pages;

    @FXML
    TableView<ReceiptReport> reports_table;

    @FXML
    TextField total_amount, number_of_donations;
    @FXML
    Label mode_of_payment;
    @FXML
    Stage stage;
    ReceiptDao receiptDao;
    DonatorService donatorService;

    ObservableList<Donators> donatorsDetail;
    @FXML
    ComboBox mode_of_payment_filter;

    @FXML
    public void initialize() {
        from_date.requestFocus();
        initializeTableColumns();
        addValidations();
        setupDatepicker();
        receiptDao = new ReceiptDao();
        donatorService = new DonatorService();
        CommonUtils.resizeColumn(reports_table);
    }

    private void setupDatepicker() {
        CommonUtils.dateConverter(from_date);
        CommonUtils.dateConverter(to_date);
        from_date.setValue(LocalDate.now());
        to_date.setValue(LocalDate.now());
    }

    private void addValidations() {
        ValidationListeners validationListeners = new ValidationListeners();
        validationListeners.validateDate(from_date);
        validationListeners.validateDate(to_date);
    }
    private void initializeTableColumns() {
        TableColumn<ReceiptReport, Integer> idColumn = new TableColumn<>("Receipt No");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("receipt_no"));

        TableColumn<ReceiptReport, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ReceiptReport, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(new Callback<TableColumn<ReceiptReport, Double>, TableCell<ReceiptReport, Double>>() {

            @Override
            public TableCell<ReceiptReport, Double> call(TableColumn<ReceiptReport, Double> donatorsDoubleTableColumn) {
                return new TableCell<ReceiptReport, Double>() {
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

        TableColumn<ReceiptReport, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<ReceiptReport, String> modeOfPayment = new TableColumn<>("Mode Of Payment");
        modeOfPayment.setCellValueFactory(new PropertyValueFactory<>("mode_of_payment"));

        TableColumn<ReceiptReport, String> mobileColumn = new TableColumn<>("Mobile No");
        mobileColumn.setCellValueFactory(new PropertyValueFactory<>("mobile_no"));

        reports_table.getColumns().addAll(idColumn, nameColumn, amountColumn, dateColumn, modeOfPayment, mobileColumn);
    }


    @FXML
    public void getReport() {
        mode_of_payment.setVisible(false);
        mode_of_payment_filter.setVisible(false);
        ObservableList<ReceiptReport> report = FXCollections.observableArrayList();

        Double totalAmount = 0.0;
        int totalEnteries = 0;
        ReceiptReport receiptReport;
        try {
            List<Donators> donatorsList = donatorService.getDonatorByDateRange(from_date.getValue(), to_date.getValue());
            for(Donators donator : donatorsList) {
                receiptReport = new ReceiptReport(donator.getReceipt_no(), donator.getName(),
                        donator.getAmount(), formatDate(donator.getDate()),
                        donator.getMode_of_payment(), donator.getMobile_no());
                totalAmount += donator.getAmount();
                totalEnteries++;
                report.add(receiptReport);
            }
            FilteredList<ReceiptReport> filteredList = new FilteredList<>(report, p -> true);
            reports_table.setItems(filteredList);

            enableModeOfPaymentFilter();
            // Add listener for the ComboBox selection
            mode_of_payment_filter.setOnAction(event -> {
                String selectedMode = (mode_of_payment_filter.getValue() == null ) ? null : mode_of_payment_filter.getValue().toString();
                filterByModeOfPayment(filteredList, selectedMode);
                updateSum(filteredList);
                updateNoOfDonations(filteredList);
            });

            //reports_table.setItems(report);
            total_amount.setText(new DecimalFormat("#.##").format(totalAmount));
            number_of_donations.setText(String.valueOf(totalEnteries));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateNoOfDonations(FilteredList<ReceiptReport> filteredList) {
        number_of_donations.setText(String.valueOf(filteredList.stream().count()));
    }

    private void enableModeOfPaymentFilter() {
        mode_of_payment.setVisible(true);
        mode_of_payment_filter.setVisible(true);
        mode_of_payment_filter.setItems(FXCollections.observableArrayList(ModeOfPayment.values()));
        mode_of_payment_filter.getItems().add("ALL");
        mode_of_payment_filter.setValue("ALL"); // Default value
    }
    @FXML
    public void printReport() throws SQLException {
        Map<String, Double> rs = receiptDao.getTotalAmountByPaymentMode(from_date.getValue(), to_date.getValue());
          JasperReportService.generateReport(reports_table, formatDate(from_date.getValue()), formatDate(to_date.getValue()),
                rs, Integer.parseInt(number_of_donations.getText()));
    }

    @FXML
    public void exportReport() throws IOException {
        ExcelFileHandler excelFileHandler = new ExcelFileHandler();
        FileChooser fileChooser = excelFileHandler.openFileSelectionWindow();
        excelFileHandler.exportTableViewToExcel(fileChooser, reports_table, "Register", "Receipt Register for the Period: " +
                formatDate(from_date.getValue()) + " To: " + formatDate(to_date.getValue()));

    }

    @FXML
    public void exportAllColumns() throws IOException {
        try {
            List<Donators> donatorsList = donatorService.getDonatorByDateRange(from_date.getValue(), to_date.getValue());
            ExcelFileHandler excelFileHandler = new ExcelFileHandler();
            FileChooser fileChooser = excelFileHandler.openFileSelectionWindow();
            excelFileHandler.exportDataToExcel(fileChooser, donatorsList, "Data", "All Data for the Period: "+
                    formatDate(from_date.getValue()) + " To: " + formatDate(to_date.getValue()), stage);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private void filterByModeOfPayment(FilteredList<ReceiptReport> filteredList, String selectedMode) {
        filteredList.setPredicate(transaction -> {
            if (selectedMode == null || selectedMode.equalsIgnoreCase("All")) {
                return true; // Show all transactions if "All" is selected
            }
            return transaction.getMode_of_payment().equalsIgnoreCase(selectedMode);
        });
    }

    private void updateSum(FilteredList<ReceiptReport> filteredList) {
        // Calculate the sum of the amounts in the filtered list
        double sum = filteredList.stream().mapToDouble(ReceiptReport::getAmount).sum();

        // Update the sum label with the calculated sum
        total_amount.setText(String.format("%.2f", sum));
    }
}
