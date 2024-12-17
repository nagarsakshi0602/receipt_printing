package com.example.receiptprinting.utils;

import com.example.receiptprinting.models.Donators;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class JasperReportUtil {
    public static void generateReport(List<Donators> donators) {
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
            parameters.put("mobile_no", propertyFileLoader.getProperty("company_mobile_no"));
            parameters.put("established_year", propertyFileLoader.getProperty("established_on_text"));
            parameters.put("email_id", propertyFileLoader.getProperty("company_email_id"));
            parameters.put("more_info", propertyFileLoader.getProperty("receipt.more_info"));
            parameters.put("receipt_no", donator.getReceipt_no());
            parameters.put("name", donator.getName());
            parameters.put("amount", donator.getAmount().intValue());
            parameters.put("mode_of_payment", donator.getMode_of_payment());
            parameters.put("date", donator.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            parameters.put("remark", donator.getRemark());
            parameters.put("amount_in_words", CommonUtils.convertToWords(donator.getAmount()) + "only");
            parameters.put("font_color", propertyFileLoader.getProperty("font_color"));
            parameters.put("border_color", propertyFileLoader.getProperty("border_color"));


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
}
