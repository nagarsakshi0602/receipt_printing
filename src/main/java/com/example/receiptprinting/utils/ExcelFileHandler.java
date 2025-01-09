package com.example.receiptprinting.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ExcelFileHandler {

    public <T> FileChooser openFileSelectionWindow() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));


        return fileChooser;
    }

    public <T> void exportTableViewToExcel(FileChooser fileChooser, TableView<T> tableView, String sheetName, String headerString) throws IOException, FileNotFoundException {

        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {

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
                try (FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath())) {
                    workbook.write(fileOut);
                }
            }
        }
    }

    public <T> void exportDataToExcel(FileChooser fileChooser, List<T> dataList, String sheetName, String headerString, Stage stage) throws IllegalAccessException, IOException {
        File file = fileChooser.showSaveDialog(stage);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // Create the header row
        Row headerRow = sheet.createRow(0);
        //sheet Header
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

        // Use reflection to get the fields of the object
        if (dataList != null && !dataList.isEmpty()) {
            T firstObject = dataList.get(0);
            Field[] fields = firstObject.getClass().getDeclaredFields();

            // Create columns for each field
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true); // Allow access to private fields
                Cell cell = tableHeader.createCell(i);
                cell.setCellValue(field.getName()); // Set the column name as the field name
            }

            // Create data rows
            int rowNum = 2;
            for (T data : dataList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    Object value = field.get(data); // Get the value of the field

                    // Create cell and set value
                    Cell cell = row.createCell(i);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    } else {
                        cell.setCellValue(""); // Handle null values
                    }
                }
            }
        }

        // Resize columns to fit content
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath())) {
            workbook.write(fileOut);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        workbook.close();
    }
}
