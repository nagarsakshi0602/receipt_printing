package com.example.receiptprinting.utils;

import com.example.receiptprinting.models.Donators;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class JasperReportUtil {
    public static void generateReceipt(List<Donators> donators) {
        try {

            // Load the .jrxml file as a resource inside the JAR
            InputStream jrxmlInputStream = JasperReportUtil.class.getResourceAsStream("/com/example/receiptprinting/receipt.jrxml");

            if (jrxmlInputStream == null) {
                System.out.println("Jasper report template not found in JAR!");
                return;
            }

            JasperReport report = JasperCompileManager.compileReport(jrxmlInputStream);

            PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
            propertyFileLoader.loadProperty("config");

            Donators donator = donators.get(0);

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("company_name", propertyFileLoader.getProperty("company_name"));
            parameters.put("company_address", propertyFileLoader.getProperty("company_address"));
            parameters.put("company_mobile_no", propertyFileLoader.getProperty("company_mobile_no"));
            parameters.put("established_year", propertyFileLoader.getProperty("established_on_text"));
            parameters.put("company_email_id", propertyFileLoader.getProperty("company_email_id"));
            parameters.put("more_info", propertyFileLoader.getProperty("receipt.more_info"));
            parameters.put("receipt_no", donator.getReceipt_no());
            parameters.put("mobile_no", donator.getMobile_no());
            parameters.put("aadhar_no", donator.getAadhar_no());
            String name = (donator.getAddress() == null || donator.getAddress().isEmpty())? "": ", "+ donator.getAddress();
            parameters.put("name", CommonUtils.toProperCase(donator.getName()) + CommonUtils.toProperCase(name));
            parameters.put("amount", donator.getAmount());
            String paymentDetails = (donator.getPayment_details() == null || donator.getPayment_details().isEmpty()) ? "" : " - " + donator.getPayment_details();
            parameters.put("mode_of_payment", donator.getMode_of_payment() + paymentDetails );
            parameters.put("date", donator.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            parameters.put("remark", CommonUtils.toProperCase(donator.getRemark()));
            String s = CommonUtils.convertToWords(donator.getAmount());
            String amount_first = null, amount_second = null;
            if( s.length() > 55){
                if(s.charAt(55) == ' '){
                    amount_first = s.substring(0,55);
                    amount_second = s.substring(56);
                }else{
                    String str = s.substring(0, 55);
                    amount_first = s.substring(0,str.lastIndexOf(" "));
                    amount_second = s.substring(str.lastIndexOf(" ") + 1);
                }
            }else{
                amount_first = s.substring(0);
            }
            parameters.put("amount_in_words", amount_first);
            parameters.put("amount_second", amount_second);
            parameters.put("font_color", propertyFileLoader.getProperty("font_color"));
            parameters.put("border_color", propertyFileLoader.getProperty("border_color"));
            parameters.put("headerImage", propertyFileLoader.getProperty("header_image_path"));


            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            JRViewer viewer = new JRViewer(print);
            viewer.setZoomRatio(0.5f);

            JFrame frame = new JFrame("Receipt Viewer");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Set desired size for the window (e.g., 800x600)
            frame.setSize(1000, 600);

            // Center the frame on the screen
            frame.setLocationRelativeTo(null);

            // Add JRViewer to the frame
            frame.getContentPane().add(viewer);

            // Make the frame visible
            frame.setVisible(true);


            //JasperExportManager.exportReportToPdfFile(print, "receipt.pdf");
            System.out.println("receipt generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void generateReport(TableView<T> tableView, String fromDate, String toDate){
        try{
            JasperReport report = loadJrxmlFile("report");
            //Assign table data to JRBean
            ObservableList<T> items = tableView.getItems();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);

            //Populating parameters from various source
            PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("company_name", propertyFileLoader.getProperty("company_name"));
            parameters.put("header", "Receipt Register for the Period: " + fromDate+" To: " + toDate);
            parameters.put("reportDataset", dataSource);

            //Filling data into the report
            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            try{
                //Opening print manager
                JasperPrintManager.printReport(print, true);
            }catch (Exception e){
                //When Printer Not found export to file
                CommonUtils.showAlert("Print Error", "Printer driver not found. File saved at applications current location");
                JasperExportManager.exportReportToPdfFile(print, "./report.pdf");
            }


        } catch (JRException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> void generateSummary(TableView<T> tableView, String fromDate, String toDate){
        try{
            JasperReport report = loadJrxmlFile("summary");
            //Assign table data to JRBean
            ObservableList<T> items = tableView.getItems();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);

            //Populating parameters from various source
            PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("company_name", propertyFileLoader.getProperty("company_name"));
            parameters.put("header", "Receipt Summary for the Period: " + fromDate+" To: " + toDate);
            parameters.put("summaryDataset", dataSource);

            //Filling data into the report
            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            try{
                //Opening print manager
                JasperPrintManager.printReport(print, true);
            }catch (Exception e){
                //When Printer Not found export to file
                CommonUtils.showAlert("Print Error", "Printer driver not found. File saved at applications current location");
                JasperExportManager.exportReportToPdfFile(print, "./summary.pdf");
            }


        } catch (JRException e) {
            throw new RuntimeException(e);
        }

    }

    public static JasperReport loadJrxmlFile(String filename){
        JasperReport report = null;

        InputStream jrxmlInputStream = JasperReportUtil.class.getResourceAsStream("/com/example/receiptprinting/" + filename + ".jrxml");

        if (jrxmlInputStream == null) {
            System.out.println("Jasper report template not found in JAR!");
            return report;
        }

        try {
             report = JasperCompileManager.compileReport(jrxmlInputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
        return report;
    }
}
