module com.example.recieptprinting {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    requires java.sql;
    requires com.h2database;
    requires java.desktop;
    requires jasperreports;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.example.receiptprinting to javafx.fxml, java.sql;

    exports com.example.receiptprinting;
    exports com.example.receiptprinting.utils;
    opens com.example.receiptprinting.utils to java.sql, javafx.fxml;
    exports com.example.receiptprinting.controllers;
    opens com.example.receiptprinting.controllers to java.sql, javafx.fxml;
    exports com.example.receiptprinting.models;
    opens com.example.receiptprinting.models to java.sql, javafx.fxml, javafx.base;
    exports com.example.receiptprinting.services;
    opens com.example.receiptprinting.services to java.sql, javafx.fxml;
}