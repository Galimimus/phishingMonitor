package com.galimimus.phishingmonitor;

import com.galimimus.phishingmonitor.controllers.NewDepartmentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewDepartmentModal {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    public static void newWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("newDepartment.fxml"));
        Stage depStage = new Stage();
        Scene scene = null;
        depStage.initModality(Modality.APPLICATION_MODAL);
        try {
            scene = new Scene(fxmlLoader.load(), 500, 110);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "NewDepartmentModal", "newWindow", e.toString());
            throw new RuntimeException(e);
        }
        depStage.setTitle("Add new department");
        depStage.setScene(scene);
        NewDepartmentController.setStage(depStage);
        depStage.showAndWait();
    }
}
