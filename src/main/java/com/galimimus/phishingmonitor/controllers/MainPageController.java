package com.galimimus.phishingmonitor.controllers;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.models.User;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextArea;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MainPageController {
    @FXML
    private AnchorPane Content;


    @FXML
    protected void MeOnClick() throws FileNotFoundException {
        //TODO: доделать вывод информации о HTTP сервере, скрыть пароль при выводе, добавить кнопку, которая позволяет увидеть
        //TODO: ПКМ по textarea - редактировать?

        DB db = new DB();
        db.connect();
        User user = db.getMe(1);
        TextArea bd_info = new TextArea("Особенности подключения к базе данных:\n username: " + db.getUsername() +
                "\n password: " + db.getPassword() + "\n host: " + db.getHost() + "\ndatabase name: " + db.getDb_name());
        db.close();
        TextArea server_info = new TextArea("Особенности настройки HTTP сервера:\n");

        Image img = new Image(new FileInputStream("/home/galimimus/IdeaProjects/phishingMonitor/src/main/resources/com/galimimus/phishingmonitor/default_person.jpg"));

        ImageView image = new ImageView(img);
        image.setFitHeight(80);
        image.setFitWidth(60);

        AnchorPane.setTopAnchor(image, 30d);
        AnchorPane.setLeftAnchor(image, 40d);


        Label name = new Label(user.getName());
        AnchorPane.setTopAnchor(name, 30d);
        AnchorPane.setLeftAnchor(name, 140d);

        Label email = new Label(user.getEmail());
        AnchorPane.setTopAnchor(email, 60d);
        AnchorPane.setLeftAnchor(email, 140d);

        Label company = new Label(user.getCompany());
        AnchorPane.setTopAnchor(company, 90d);
        AnchorPane.setLeftAnchor(company, 140d);

        //bd_info.setPrefSize(300, 150);
        AnchorPane.setTopAnchor(bd_info, 150d);
        AnchorPane.setLeftAnchor(bd_info, 40d);
        AnchorPane.setRightAnchor(bd_info, 40d);


        //server_info.setPrefSize(400, 150);
        AnchorPane.setTopAnchor(server_info, 350d);
        AnchorPane.setRightAnchor(server_info, 40d);
        AnchorPane.setLeftAnchor(server_info, 40d);


        Content.getChildren().add(name);
        Content.getChildren().add(email);
        Content.getChildren().add(company);
        Content.getChildren().add(image);
        Content.getChildren().add(bd_info);
        Content.getChildren().add(server_info);
    }
    @FXML
    protected void EmployeesOnClick(){
        DB db = new DB();
        db.connect();
        ArrayList<Employee> employees = db.getEmployees();
        db.close();
        AnchorPane menu = new AnchorPane();
        Content.getChildren().add(menu);

        //TODO:поменять выдачу, а то получается херня а не разделение. Для отделов использовать menubutton?
    }
    @FXML
    protected void MailingsOnClick(){

    }
    @FXML
    protected void StatisticOnClick(){

    }
    @FXML
    protected void NewMailingOnClick(){

    }
}
