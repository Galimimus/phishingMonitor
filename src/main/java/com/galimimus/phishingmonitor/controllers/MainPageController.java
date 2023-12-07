package com.galimimus.phishingmonitor.controllers;

import com.galimimus.phishingmonitor.StartApplication;
import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.SettingsSingleton;
import com.galimimus.phishingmonitor.helpers.Validation;
import com.galimimus.phishingmonitor.mailings.EXEMailing;
import com.galimimus.phishingmonitor.mailings.QRMailing;
import com.galimimus.phishingmonitor.mailings.URLMailing;
import com.galimimus.phishingmonitor.models.Department;
import com.galimimus.phishingmonitor.models.Employee;
import com.galimimus.phishingmonitor.mailings.Mailing;
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


import javax.mail.MessagingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;





public class MainPageController {
    @FXML
    private AnchorPane Content;
    private AnchorPane HelpContent = new AnchorPane();
    final DB db = new DB();
    static final Logger log = Logger.getLogger(StartApplication.class.getName());


    @FXML
    protected void MeOnClick() throws FileNotFoundException {
        //TODO: скрыть пароль при выводе, добавить кнопку, которая позволяет увидеть
        //TODO: ПКМ по textarea - редактировать?                        | сделать действия на клик по textarea
        //TODO: кнопки для запуска/остановки/рестарта сервака, ПКМ??    | потом
        Content.getChildren().clear();
        db.connect();
        User user = db.getMe(1);
        db.close();
        SettingsSingleton ss = SettingsSingleton.getInstance();
        TextArea bd_info = new TextArea("Особенности подключения к базе данных:\nusername: " + ss.getDB_USERNAME() +
                "\npassword: " + ss.getDB_PASS() + "\nhost: " + ss.getDB_HOST() + "\ndatabase name: " + ss.getDB_NAME());

        TextArea server_info = new TextArea("Особенности настройки HTTP сервера:\nhost: " + ss.getHTTP_SERVER_HOST()
                +"\nport: " + ss.getHTTP_SERVER_PORT()+"\nlog employee url: "+ "http://"+ss.getHTTP_SERVER_HOST()
                +":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_URL_HANDLE()+
                "\ndownload file url: "+ "http://"+ss.getHTTP_SERVER_HOST()
                +":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_DOWNLOAD_HANDLE()+
                "\nbypass mail check url: "+ "http://"+ss.getHTTP_SERVER_HOST()
                +":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_EXE_HANDLE());

        Image img = null;
        img = new Image(Objects.requireNonNull(StartApplication.class.getResourceAsStream("default_person.jpg")));

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
                btn.setOnAction(actionEvent -> EmployeeInfoOnClick(btn.getId()));
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
        Button btn1 = new Button("Вставить гиперссылку");//hyperlink word
        btn1.setDisable(true);
        btn1.setOnAction(actionEvent -> mail_text.appendText("<a href=\"\">Отображаемый текст</a>"));

        CheckBox check0 = new CheckBox();//exe
        check0.setText("сгенерировать exe");

        CheckBox check1 = new CheckBox();//qr
        check1.setText("сгенерировать QR");
        CheckBox check2 = new CheckBox();//url\
        check2.setText("сгенерировать URL");

        check0.setOnAction(actionEvent -> {
            if (check0.isSelected()) {
                check1.setDisable(true);
                check2.setDisable(true);
                btn1.setDisable(false);
            } else {
                check1.setDisable(false);
                check2.setDisable(false);
                btn1.setDisable(true);
            }
        });

        check1.setOnAction(actionEvent -> {
            if (check1.isSelected()) {
                check0.setDisable(true);
                check2.setDisable(true);
            } else {
                check0.setDisable(false);
                check2.setDisable(false);
            }
        });

        check2.setOnAction(actionEvent -> {
            if (check2.isSelected()) {
                check0.setDisable(true);
                check1.setDisable(true);
                btn1.setDisable(false);
            } else {
                check0.setDisable(false);
                check1.setDisable(false);
                btn1.setDisable(true);
            }
        });
        ComboBox<String> combo = new ComboBox<>();//groups
        combo.getItems().add("все");
        db.connect();
        ArrayList<Department> deps = db.getDepartments();
        db.close();
        for(Department dep : deps){
            combo.getItems().add(dep.getName());
        }

        //TODO: доделать проверку заполнения полей. Кнопки и текстфилды для вставки картинки. Текстфилды для ввода email.

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
        mail_settings.getChildren().add(btn1);
        mail_settings.getChildren().add(label6);
        mail_settings.getChildren().add(btn0);

        btn0.setOnAction(actionEvent -> {

            String text = mail_text.getText();
            String recipients = combo.getValue();
            String theme = field1.getText();
            String email = field0.getText();
            if (Validation.isNullOrEmpty(text) || Validation.isNullOrEmpty(recipients) || Validation.isNullOrEmpty(email)) {
                label6.setVisible(true);
                label6.setText("Заполните необходимые поля");
                return;
            }
            if (Validation.validatePattern(email, Pattern.compile("^[_A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))) {
                label6.setVisible(true);
                label6.setText("Email имеет недопустимый формат");
                return;
            }

            SettingsSingleton ss = SettingsSingleton.getInstance();
            if (check0.isSelected()) {//exe
                Thread mailing = new Thread(new EXEMailing(text, recipients, theme));
                mailing.start();
                label6.setVisible(false);
            } else if (check1.isSelected()) {
                Thread mailing = new Thread(new QRMailing(text, recipients, theme));
                mailing.start();
                label6.setVisible(false);
            } else if (check2.isSelected()) {
                Thread mailing = new Thread(new URLMailing(text, recipients, theme));
                mailing.start();
                label6.setVisible(false);
            } else {
                label6.setVisible(true);
                label6.setText("Выберите тип полезной нагрузки");
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
            Employee emp = db.getEmployee(id.substring(3));
            db.close();
            Image img;
            try {
                img = new Image(new FileInputStream("src/main/resources/com/galimimus/phishingmonitor/default_person.jpg"));
            } catch (FileNotFoundException e) {
                log.logp(Level.SEVERE, "MainPageController", "EmployeeInfoOnClick", e.toString());
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
            AnchorPane.setTopAnchor(email, 60d);
            AnchorPane.setLeftAnchor(email, 140d);

            Label ip = new Label(emp.getIp());
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
