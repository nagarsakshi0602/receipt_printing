package com.example.receiptprinting;

import com.example.receiptprinting.utils.CommonUtils;
import com.example.receiptprinting.utils.PropertyFileLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.h2.tools.Server;

import java.io.IOException;
import java.sql.SQLException;

public class ReceiptApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Server server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8081").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("H2 Console started at http://localhost:8081");
        FXMLLoader fxmlLoader = new FXMLLoader(ReceiptApplication.class.getResource("Form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
        propertyFileLoader.loadProperty("config");
        stage.setTitle(propertyFileLoader.getProperty("companyName"));
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            if (CommonUtils.confirmationAlert("Close Application", "Do you want to close this application?")) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}