package com.galimimus.phishingmonitor;

import com.galimimus.phishingmonitor.server.HTTPServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("mainpage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        HTTPServer server = new HTTPServer();
        server.startHttpServer();
    }

    public static void main(String[] args) {
        launch();
    }
}