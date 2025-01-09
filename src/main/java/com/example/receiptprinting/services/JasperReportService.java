package com.example.receiptprinting.services;

import com.example.receiptprinting.models.Donators;
import com.example.receiptprinting.models.ModeOfPayment;
import com.example.receiptprinting.utils.CommonUtils;
import com.example.receiptprinting.utils.JasperReportUtil;
import com.example.receiptprinting.utils.PropertyFileLoader;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.receiptprinting.utils.JasperReportUtil.loadJrxmlFile;

public class JasperReportService {

    public static void generateReceipt(List<Donators> donators) {
        try {

            JasperReport report = loadJrxmlFile("receipt");

            PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
            propertyFileLoader.loadProperty("config");

            Donators donator = donators.get(0);
            HashMap<String, Object> parameters = getReceiptParameters(propertyFileLoader, donator);

            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

            JasperViewer viewer = new JasperViewer(print);
            viewer.setZoomRatio(0.5f);
            viewer.setVisible(true);

            JasperReportUtil.customPrinterSetting(print,viewer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Object> getReceiptParameters(PropertyFileLoader propertyFileLoader, Donators donator) {
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

        //Check if get address not present
        String name = (donator.getAddress() == null || donator.getAddress().isEmpty())? "": ", "+ donator.getAddress();
        parameters.put("name", CommonUtils.toProperCase(donator.getName()) + CommonUtils.toProperCase(name));
        parameters.put("amount", donator.getAmount());

        //Check if Payment details not present
        String paymentDetails = (donator.getPayment_details() == null || donator.getPayment_details().isEmpty()) ? "" : " - " + donator.getPayment_details();
        parameters.put("mode_of_payment", donator.getMode_of_payment() + paymentDetails );
        parameters.put("date", donator.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        parameters.put("remark", CommonUtils.toProperCase(donator.getRemark()));

        //Checking for amount in words overflow
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
        return parameters;
    }

    public static <T> void generateReport(TableView<T> tableView, String fromDate, String toDate, Map<String, Double> modewiseAmount, Integer total_donations){
        try{
            JasperReport report = loadJrxmlFile("report");
            //Assign table data to JRBean
            ObservableList<T> items = tableView.getItems();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);

            //Populating parameters from various source
            Double total_cash = (modewiseAmount.get(ModeOfPayment.CASH.toString())) == null ? 0.0 : modewiseAmount.get(ModeOfPayment.CASH.toString());
            Double total_cheque = (modewiseAmount.get(ModeOfPayment.CHEQUE.toString())) == null ? 0.0 : modewiseAmount.get(ModeOfPayment.CHEQUE.toString());
            Double total_online = (modewiseAmount.get(ModeOfPayment.ONLINE_TRANSFER.toString())) == null ? 0.0 : modewiseAmount.get(ModeOfPayment.ONLINE_TRANSFER.toString());
            PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("company_name", propertyFileLoader.getProperty("company_name"));
            parameters.put("header", "Receipt Register for the Period: " + fromDate+" To: " + toDate);
            parameters.put("reportDataset", dataSource);
            parameters.put("total_cash_collected", total_cash);
            parameters.put("total_cheque_collected",total_cheque);
            parameters.put("total_online_collected",total_online);
            parameters.put("total_amount_collected", total_cash+total_cheque+total_online);
            parameters.put("no_of_donations", total_donations);

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
}
