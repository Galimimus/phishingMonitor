package com.galimimus.phishingmonitor;

import com.galimimus.phishingmonitor.controllers.NewEmployeeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewEmployeeModal {
    static final Logger log = Logger.getLogger(StartApplication.class.getName());
    public static void newWindow(int depId){
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("newEmployee.fxml"));
        Stage empStage = new Stage();
        Scene scene;
        empStage.initModality(Modality.APPLICATION_MODAL);
        try {
            scene = new Scene(fxmlLoader.load(), 400, 450);
        } catch (IOException e) {
            log.logp(Level.SEVERE, "NewEmployeeModal", "newWindow", e.toString());
            throw new RuntimeException(e);
        }
        empStage.setTitle("Add new employee");
        empStage.setScene(scene);
        NewEmployeeController.setDepId(depId);
        NewEmployeeController.setStage(empStage);
        empStage.showAndWait();
    }
}
