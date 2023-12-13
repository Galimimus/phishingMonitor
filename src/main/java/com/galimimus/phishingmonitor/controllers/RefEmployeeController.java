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

public class RefEmployeeController {
    public Button cancelBtn;

    public TextField name;
    public TextField ip;
    public TextField email;

    public TextField depName;
    public Button createBtn;

    public Label info;
    @Setter
    private static Stage stage;
    static final Logger log = Logger.getLogger(StartApplication.class.getName());

    @FXML
    protected void cancelBtnOnClick(){
        stage.close();
    }
    @FXML
    public void createBtnOnClick() {
        String empName = name.getText();
        String empIp = ip.getText();
        String empEmail = email.getText();
        String empDepName = depName.getText();
        if(Validation.isNullOrEmpty(empName) || Validation.isNullOrEmpty(empIp) || Validation.isNullOrEmpty(empEmail) || Validation.isNullOrEmpty(empDepName) ){
            log.logp(Level.WARNING, "RefEmployeeController", "createBtnOnClick", "Не все поля заполнены");
            info.setText("Не все поля заполнены");
            info.setTextFill(Color.DARKRED);
            return;
        }
        if(Validation.validateSymbols(empName) || Validation.validateSymbols(empIp) || Validation.validateSymbols(empEmail) || Validation.isNullOrEmpty(empDepName)){
            log.logp(Level.WARNING, "RefEmployeeController", "createBtnOnClick", "Недопустимые символы в введенных данных:" +
                    " name = "+empName+" ip = "+empIp+" email = "+empEmail+" depName = "+empDepName);
            info.setText("Недопустимые символы в введенных данных.");
            info.setTextFill(Color.DARKRED);
            return;
        }
        DB db = new DB();
        db.connect();
        int res = db.updateEmployee(empName, empIp, empEmail, empDepName);
        db.close();
        if(res==1){
            info.setText("Сотрудник "+empName+" успешно обновлен");
            info.setTextFill(Color.DARKGREEN);
        }else if(res == 2){
            info.setText("Указан несуществующий отдел");
            info.setTextFill(Color.DARKRED);
        }else{
            info.setText("При обновлении сотрудника "+empName+" произошла ошибка");
            info.setTextFill(Color.DARKRED);
        }
    }

}
