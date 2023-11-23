package com.galimimus.phishingmonitor.controllers;

import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.models.Department;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.models.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainPageController {
    @FXML
    private AnchorPane Content;
    private AnchorPane HelpContent = new AnchorPane();
    DB db = new DB();



    @FXML
    protected void MeOnClick() throws FileNotFoundException {
        //TODO: доделать вывод информации о HTTP сервере, скрыть пароль при выводе, добавить кнопку, которая позволяет увидеть
        //TODO: ПКМ по textarea - редактировать?
        Content.getChildren().clear();
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
        Content.getChildren().clear();
        //TODO:ПКМ добавление отдела и добавление сотрудника + в панельке задач сделать возможность
        db.connect();
        HashMap<String, ArrayList<Employee>> departments = db.getEmployees();
        db.close();
        Accordion menu = new Accordion();
        for(Map.Entry<String, ArrayList<Employee>> department : departments.entrySet()){
            VBox p_content = new VBox();
            p_content.setMinWidth(150d);

            TitledPane pane = new TitledPane(department.getKey(), p_content);

            for(Employee emp : department.getValue()){
                Button btn = new Button(emp.getName());
                btn.setId("emp"+emp.getId());
                btn.setMinWidth(p_content.getMinWidth());
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        EmployeeInfoOnClick(btn.getId());
                    }
                });
                p_content.getChildren().add(btn);

            }
            menu.getPanes().add(pane);
        }
        Content.getChildren().add(menu);
    }
    @FXML
    protected void MailingsOnClick(){

    }
    @FXML
    protected void StatisticOnClick(){

    }
    @FXML
    protected void NewMailingOnClick(){
        Content.getChildren().clear();

        TextArea mail_text = new TextArea();

        AnchorPane.setTopAnchor(mail_text, 60d);
        AnchorPane.setLeftAnchor(mail_text, 40d);
        mail_text.setPrefSize(305d, 420d);

        VBox mail_settings = new VBox();

        AnchorPane.setTopAnchor(mail_settings, 30d);
        AnchorPane.setRightAnchor(mail_settings, 40d);

        Label label0 = new Label("Тема письма:");
        Label label1 = new Label("Текст письма:");

        AnchorPane.setTopAnchor(label1, 30d);
        AnchorPane.setLeftAnchor(label1, 40d);

        Label label2 = new Label("Email для рассылки:");
        Label label3 = new Label("Выберите участников рассылки:");
        Label label4 = new Label("Полезная нагрузка для рассылки:");
        Label label5 = new Label("Гиперссылка:");
        Label label6 = new Label("INFO");
        label6.setVisible(false);
        Button btn0 = new Button("Начать рассылку");
        TextField field0 = new TextField();//email
        TextField field1 = new TextField();//theme
        TextField field2 = new TextField();//hyperlink word
        CheckBox check0 = new CheckBox();//exe
        check0.setText("сгенерировать exe");
        CheckBox check1 = new CheckBox();//qr
        check1.setText("сгенерировать QR");
        CheckBox check2 = new CheckBox();//url\
        check2.setText("сгенерировать URL");
        ComboBox<String> combo = new ComboBox<>();//groups
        combo.getItems().add("все");
        db.connect();
        ArrayList<Department> deps = db.getDepartments();
        db.close();
        for(Department dep : deps){
            combo.getItems().add(dep.getName());
        }
        ProgressBar progress = new ProgressBar();//mailing
        progress.setVisible(false);
        //TODO: доделать проверку заполнения полей

        mail_settings.getChildren().add(label0);
        mail_settings.getChildren().add(field1);
        mail_settings.getChildren().add(label2);
        mail_settings.getChildren().add(field0);
        mail_settings.getChildren().add(label3);
        mail_settings.getChildren().add(combo);
        mail_settings.getChildren().add(label4);
        mail_settings.getChildren().add(check0);
        mail_settings.getChildren().add(check1);
        mail_settings.getChildren().add(check2);
        mail_settings.getChildren().add(label5);
        mail_settings.getChildren().add(field2);
        mail_settings.getChildren().add(label6);
        mail_settings.getChildren().add(btn0);
        mail_settings.getChildren().add(progress);

        btn0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!Objects.equals(mail_text.getText(), "") && (check0.isSelected() ||
                        check1.isSelected() || check2.isSelected()) && combo.getValue() != null
                && !Objects.equals(field0.getText(), "")){
                    if(check0.isSelected()) {//TODO: ЭТО ЖУТКИЕ КОСТЫЛИ!!! ПЕРЕДЕЛАТЬ!!!
                        //PrepareMail(mail_text, check0, combo.getValue(), field0.getText(), );
                    }
                }else{
                    label6.setVisible(true);
                    label6.setText("Заполните необходимые поля");
                }
            }
        });

        Content.getChildren().add(label1);
        Content.getChildren().add(mail_text);
        Content.getChildren().add(mail_settings);
    }
    protected void EmployeeInfoOnClick(String id){
        HelpContent.getChildren().clear();
        HelpContent = new AnchorPane();
        AnchorPane.setRightAnchor(HelpContent,40d);
        AnchorPane.setLeftAnchor(HelpContent, 180d);

            db.connect();
            Employee emp = db.getEmployee(id);
            db.close();
            Image img = null;
            try {
                img = new Image(new FileInputStream("src/main/resources/com/galimimus/phishingmonitor/default_person.jpg"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            ImageView image = new ImageView(img);
            image.setFitHeight(80);
            image.setFitWidth(60);

            AnchorPane.setTopAnchor(image, 30d);
            AnchorPane.setLeftAnchor(image, 40d);


            Label name = new Label(emp.getName());
            AnchorPane.setTopAnchor(name, 30d);
            AnchorPane.setLeftAnchor(name, 140d);

            Label email = new Label(emp.getEmail());
            System.out.println(emp.getEmail());
            AnchorPane.setTopAnchor(email, 60d);
            AnchorPane.setLeftAnchor(email, 140d);

            Label ip = new Label(emp.getIP());
            AnchorPane.setTopAnchor(ip, 90d);
            AnchorPane.setLeftAnchor(ip, 140d);

            Label dep = new Label(emp.getDepartment().getName());
            AnchorPane.setTopAnchor(dep, 120d);
            AnchorPane.setLeftAnchor(dep, 140d);

            Circle circle = new Circle();
            circle.setRadius(40d);
            AnchorPane.setTopAnchor(circle, 60d);
            AnchorPane.setLeftAnchor(circle, 300d);
            Label rait = new Label(Integer.toString(emp.getRaiting()));
            AnchorPane.setTopAnchor(rait, 95d);
            AnchorPane.setLeftAnchor(rait, 335d);
            if(emp.getRaiting() == 0) {
                circle.setFill(Color.LIGHTGRAY);
            }else if(emp.getRaiting() < 5){
                circle.setFill(Color.RED);
            }else if(emp.getRaiting() > 8) {
                circle.setFill(Color.GREEN);
            }else{
                circle.setFill(Color.YELLOW);
            }
            HelpContent.getChildren().add(name);
            HelpContent.getChildren().add(email);
            HelpContent.getChildren().add(dep);
            HelpContent.getChildren().add(image);
            HelpContent.getChildren().add(ip);
            HelpContent.getChildren().add(circle);
            HelpContent.getChildren().add(rait);

            Content.getChildren().add(HelpContent);
        }
}
