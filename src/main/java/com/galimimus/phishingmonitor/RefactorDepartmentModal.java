package com.galimimus.phishingmonitor;

import com.galimimus.phishingmonitor.controllers.RefDepartmentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RefactorDepartmentModal {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public static void newWindow(int id) {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("refactorDepartment.fxml"));
        Stage depStage = new Stage();
        Scene scene;
        depStage.initModality(Modality.NONE);
        try {
            scene = new Scene(fxmlLoader.load(), 500, 110);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "RefactorDepartmentModal", "newWindow", e.toString());
            throw new RuntimeException(e);
        }
        depStage.setTitle("Refactor department");
        depStage.setScene(scene);
        RefDepartmentController.setStage(depStage);
        depStage.showAndWait();
    }
}
