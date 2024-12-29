package com.example.receiptprinting.controllers;

import com.example.receiptprinting.models.Donators;
import com.example.receiptprinting.models.ReceiptReport;
import com.example.receiptprinting.utils.CommonUtils;
import com.example.receiptprinting.utils.DatabaseUtil;
import com.example.receiptprinting.utils.ExcelFileHandler;
import com.example.receiptprinting.utils.ValidationListeners;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportController {

    @FXML
    DatePicker from_date, to_date;
    @FXML
    Pagination pages;

    @FXML
    TableView<ReceiptReport> reports_table;

    @FXML
    TextField total_amount, number_of_donations;

    ObservableList<Donators> donatorsDetail;
    private final int rowsPerPage = 10;

    @FXML
    public void initialize() {
        from_date.requestFocus();
        initializeTableColumns();
        ValidationListeners validationListeners = new ValidationListeners();
        validationListeners.validateDate(from_date);
        validationListeners.validateDate(to_date);
        CommonUtils.dateConverter(from_date);
        CommonUtils.dateConverter(to_date);
        from_date.setValue(LocalDate.now());
        to_date.setValue(LocalDate.now());

        CommonUtils.resizeColumn(reports_table);
        //setupPagination();
    }



    @FXML
    public void getReport() {
        ObservableList<ReceiptReport> report = FXCollections.observableArrayList();
        Double totalAmount = 0.0; int totalEnteries = 0; ReceiptReport receiptReport;
        try {
            ResultSet rs = DatabaseUtil.getDonatorsReport(from_date.getValue(), to_date.getValue());
            while(rs.next()){
                receiptReport = new ReceiptReport(rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("amount"), rs.getDate("date").toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        rs.getString("mode_of_payment"), rs.getString("mobile_no"));
                totalAmount += rs.getDouble("amount");
                totalEnteries++;
                report.add(receiptReport);
            }
            reports_table.setItems(report);
            total_amount.setText(new DecimalFormat("#.##").format(totalAmount));
            number_of_donations.setText(String.valueOf(totalEnteries));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    private void setupPagination() {
       /* int totalPages = (int) Math.ceil((double) donatorsDetail.size() / rowsPerPage);
        pages.setPageCount(totalPages);
        pages.setCurrentPageIndex(0);
        pages.setPageFactory(this::createPage);*/
    }

    /*private TableView<Donators> createPage(int pageIndex) {
        int start = pageIndex * rowsPerPage;
        int end = Math.min(start + rowsPerPage, donatorsDetail.size());

        ObservableList<ReceiptReport> pageData = FXCollections.observableArrayList(donatorsDetail.subList(start, end));
        reports_table.setItems(pageData);

        return reports_table;
    }*/


    @FXML
    public void printReport() {
        /*Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            System.out.println("No printers found.");
            return;
        }*/

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(reports_table.getScene().getWindow())) {
            // Get the printable area
            PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
            double printableWidth = pageLayout.getPrintableWidth();
            double printableHeight = pageLayout.getPrintableHeight();

            // Scale the table to fit the printable area
            double scaleX = printableWidth / reports_table.getBoundsInParent().getWidth();
            double scaleY = printableHeight / reports_table.getBoundsInParent().getHeight();
            double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio

            reports_table.setScaleX(scale);
            reports_table.setScaleY(scale);

            // Print the scaled table
            boolean success = printerJob.printPage(reports_table);

            // Reset scaling to avoid affecting the UI
            reports_table.setScaleX(1.0);
            reports_table.setScaleY(1.0);

            if (success) {
                printerJob.endJob();
                System.out.println("Printing completed successfully.");
            } else {
                System.out.println("Printing failed.");
            }
        } else {
            System.out.println("Printing canceled.");
        }
    }

    @FXML
    public void exportReport() throws IOException {

        new ExcelFileHandler().openFileSelectionWindow(reports_table, "Register", "Receipt Register for the Period: " +
                from_date.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " To: " + to_date.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }
}
