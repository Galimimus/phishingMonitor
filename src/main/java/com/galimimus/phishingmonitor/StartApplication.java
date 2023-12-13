package com.galimimus.phishingmonitor;

import com.galimimus.phishingmonitor.server.HTTPServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.LogManager;

public class StartApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("mainPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Phishing Monitor");
        stage.setScene(scene);
        stage.show();
        HTTPServer.startHttpServer();
        try {
            LogManager.getLogManager().readConfiguration(
                    StartApplication.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }

        stage.setOnCloseRequest(windowEvent -> {
            //HTTPServer.stopHttpServer();
            Platform.exit();
            System.exit(0);
        });

    }

    public static void main(String[] args) {
        launch();
    }
}
//TODO:
// 1. Autofill department when adding employee.
// 2. Add remove employee, department, mailing
// (add link to destroy last_mailing elements when deleting mailing,destroy employees when deleting department).
// 3. Add refactor setting.
// 4. Think about installer.
// 5. Try it on Windows.