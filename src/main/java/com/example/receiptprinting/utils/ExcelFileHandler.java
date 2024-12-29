package com.example.receiptprinting.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelFileHandler {

    public <T> File openFileSelectionWindow(TableView<T> tableView, String sheetName, String headerString) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file =  fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {
            ExcelFileHandler.exportToExcel(tableView, sheetName, file.getAbsolutePath(), headerString);
        }
        return file;
    }

    public static <T> void exportToExcel(TableView<T> tableView, String sheetName, String filePath, String headerString) throws IOException, FileNotFoundException {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet(sheetName);

            ObservableList<TableColumn<T, ?>> columns = tableView.getColumns();

            //sheet Header
            Row headerRow = sheet.createRow(0);
            Cell headerRowCell = headerRow.createCell(0);
            headerRowCell.setCellValue(headerString);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
            // Create a cell style for the header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setFontName("Times New Roman");
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14); // Set font size
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER); // Center text
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); //
            headerRowCell.setCellStyle(headerStyle);
            // Create header row
            Row tableHeader = sheet.createRow(1);

            for (int i = 0; i < columns.size(); i++) {
                tableHeader.createCell(i).setCellValue(columns.get(i).getText());
            }

            // Fill data
            ObservableList<T> items = tableView.getItems();

            for (int rowIdx = 0; rowIdx < items.size(); rowIdx++) {

                Row row = sheet.createRow(rowIdx + 2);
                T item = items.get(rowIdx);

                for (int colIdx = 0; colIdx < columns.size(); colIdx++) {

                    Object cellValue = columns.get(colIdx).getCellData(item);

                    if (cellValue != null) {
                        row.createCell(colIdx).setCellValue(cellValue.toString());
                    }
                }
            }

            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Save the workbook to a file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
}
