package com.example.receiptprinting;

import com.example.receiptprinting.utils.CommonUtils;
import com.example.receiptprinting.utils.DatabaseInitializer;
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

        //Creating a Webserver to access database
        PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();
        propertyFileLoader.loadProperty("config");
        String port = propertyFileLoader.getProperty("port");
        System.out.println(port);
        try {
            Server server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", port).start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("H2 Console started at http://localhost:" + port);

        //Initializing Database
        DatabaseInitializer.initializeDatabase();

        //Loading FXML file to create the application window
        FXMLLoader fxmlLoader = new FXMLLoader(ReceiptApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

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