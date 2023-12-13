package com.galimimus.phishingmonitor.controllers;

import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.Validation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Setter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RefDepartmentController {
    public Button cancelBtn;

    public TextField name;

    public Button createBtn;

    public Label info;
    @Setter
    private static int id;
    @Setter
    private static Stage stage;
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    @FXML
    protected void cancelBtnOnClick(){
        stage.close();
    }
    @FXML
    public void createBtnOnClick() {
        String dep = name.getText();
        if(Validation.isNullOrEmpty(dep)){
            log.logp(Level.WARNING, "RefDepartmentController", "createBtnOnClick", "Введите название отдела");
            info.setText("Введите название отдела");
            info.setTextFill(Color.DARKRED);
            return;
        }
        if(Validation.validateSymbols(dep)){
            log.logp(Level.WARNING, "RefDepartmentController", "createBtnOnClick", "Название отдела: "+dep+" содержит недопустимые символы");
            info.setText("Название отдела: "+dep+" содержит недопустимые символы");
            info.setTextFill(Color.DARKRED);
            return;
        }
        DB db = new DB();
        db.connect();
        int res = db.updateDepartment(dep, id);
        db.close();
        if(res==1){
            info.setText("Отдел "+dep+" успешно обновлен");
            info.setTextFill(Color.DARKGREEN);
        }else{
            info.setText("При обновлении отдела "+dep+" произошла ошибка");
            info.setTextFill(Color.DARKRED);
        }
    }
}
