package com.galimimus.phishingmonitor;

import com.galimimus.phishingmonitor.controllers.RefEmployeeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RefactorEmployeeModal {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());
    public static void newWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("refactorEmployee.fxml"));
        Stage empStage = new Stage();
        Scene scene;
        empStage.initModality(Modality.NONE);
        try {
            scene = new Scene(fxmlLoader.load(), 400, 450);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "RefactorEmployeeModal", "newWindow", e.toString());
            throw new RuntimeException(e);
        }
        empStage.setTitle("Refactor employee");
        empStage.setScene(scene);
        RefEmployeeController.setStage(empStage);
        empStage.showAndWait();
    }
}
