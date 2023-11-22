package com.galimimus.phishingmonitor.controllers;

import com.galimimus.phishingmonitor.server.StartHTTPServer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StartController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws Throwable {
        welcomeText.setText("Welcome to JavaFX Application!");
        StartHTTPServer server = new StartHTTPServer();
        server.start();
    }
}