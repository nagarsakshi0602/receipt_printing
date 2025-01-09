package com.example.receiptprinting.utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.InputStream;

public class JasperReportUtil {

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

    public static void receiptViewer(JasperPrint print, JRViewer viewer) {
        // Create a JFrame to hold the JRViewer
        JFrame frame = new JFrame("Receipt Viewer");
        frame.getContentPane().add(viewer);
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add a custom print button to the JFrame
        JButton printButton = new JButton("Print");
        printButton.setPreferredSize(new Dimension(100, 25));
        printButton.setBorder(new LineBorder(Color.BLACK, 1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(printButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        System.out.println("receipt generated successfully!");

        //Customizing printer setting with default copies
        //customPrinterSetting(print, printButton, frame);

        frame.setVisible(true);
    }

    private static JButton getPrintButton(JasperViewer viewer) {
        JButton jButton = null;
        for(Component component: viewer.getContentPane().getComponents()){
            if(component instanceof JPanel){
                JPanel panel = (JPanel) ((JPanel) component).getComponent(0);
                for(Component c : panel.getComponents()){
                    Component[] jp =  ((JPanel) c).getComponents();
                    for(Component j: jp){
                        if(j instanceof JButton){
                            if(((JButton) j).getToolTipText().equalsIgnoreCase("Print")){
                                jButton = (JButton) j;
                            }
                        }
                    }
                }
            }
        }
        return jButton;
    }


    public static void customPrinterSetting(JasperPrint print, JasperViewer viewer){
        JButton printButton = getPrintButton(viewer);
        printButton.addActionListener(e -> {
            try {
                // Default number of copies
                int defaultCopies = 2;

                // Get the default printer
                PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

                if (printService != null) {
                    // Create a PrinterJob for customized print settings
                    PrinterJob printerJob = PrinterJob.getPrinterJob();
                    printerJob.setPrintService(printService);
                    printerJob.setCopies(2);
                    // Set print attributes (number of copies)
                    if(printerJob.printDialog()){
                        printerJob.setCopies(defaultCopies);
                        printerJob.setPrintable(new JasperPrintable(print));
                        printerJob.print();
                    }
                } else {
                    JOptionPane.showMessageDialog(viewer, "No printer found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (PrinterException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(viewer, "An error occurred during printing: " + ex.getMessage(),
                       "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    // Helper class to render the JasperPrint content to a Printable object
    public static class JasperPrintable implements Printable {
        private JasperPrint jasperPrint;

        public JasperPrintable(JasperPrint jasperPrint) {
            this.jasperPrint = jasperPrint;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex >= jasperPrint.getPages().size()) {
                return NO_SUCH_PAGE;
            }

            // Render the page using JasperReports rendering engine
            try {
                JasperPrintManager.printPage(jasperPrint, pageIndex, true);
            } catch (JRException e) {
                throw new RuntimeException(e);
            }
            return PAGE_EXISTS;
        }
    }

}
