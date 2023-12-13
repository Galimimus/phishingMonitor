
package com.galimimus.phishingmonitor.controllers;

import com.galimimus.phishingmonitor.*;
import com.galimimus.phishingmonitor.helpers.DB;
import com.galimimus.phishingmonitor.helpers.SettingsSingleton;
import com.galimimus.phishingmonitor.helpers.Validation;
import com.galimimus.phishingmonitor.mailings.EXEMailing;
import com.galimimus.phishingmonitor.mailings.QRMailing;
import com.galimimus.phishingmonitor.mailings.URLMailing;
import com.galimimus.phishingmonitor.models.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;


import java.util.*;


import static com.galimimus.phishingmonitor.logic.Statistic.*;


public class MainPageController {

    @FXML
    private HBox Content;
    final AnchorPane HelpContent = new AnchorPane();
    final DB db = new DB();
    //static final Logger log = Logger.getLogger(StartApplication.class.getName());


    @FXML
    protected void MeOnClick(){
        Content.getChildren().clear();
        db.connect();
        User user = db.getMe(1);
        db.close();
        SettingsSingleton ss = SettingsSingleton.getInstance();
        Label work_dir = new Label("Working directory:\n " + ss.getWORKING_DIRECTORY());
        TextArea bd_info = new TextArea("Особенности подключения к базе данных:\nusername: " + ss.getDB_USERNAME() +
                "\npassword: " + ss.getDB_PASS() + "\nhost: " + ss.getDB_HOST() + "\ndatabase name: " + ss.getDB_NAME());

        TextArea server_info = new TextArea("Особенности настройки HTTP сервера:\nhost: " + ss.getHTTP_SERVER_HOST()
                +"\nport: " + ss.getHTTP_SERVER_PORT()+"\nlog employee url: "+ "http://"+ss.getHTTP_SERVER_HOST()
                +":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_URL_HANDLE()+
                "\ndownload file url: "+ "http://"+ss.getHTTP_SERVER_HOST()
                +":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_DOWNLOAD_HANDLE()+
                "\nbypass mail check url: "+ "http://"+ss.getHTTP_SERVER_HOST()
                +":"+ss.getHTTP_SERVER_PORT()+"/"+ss.getHTTP_SERVER_EXE_HANDLE());
        TextArea mail_info = new TextArea("Особенности рассылок:\nemail from: " + ss.getUSER_EMAIL() +
                "\npassword for application: " + ss.getUSER_APP_PASS() + "\nsmtp server: " + ss.getMAIL_SMTP_SERVER() +
                "\nsmtp port: " + ss.getMAIL_SMTP_PORT() + "\nmingw compiler command for operating system: " + ss.getMINGW_COMMAND()
                + "\nrar archiver command for operating system: " + ss.getRAR_COMMAND()
                + "\ncp (copy files) command for operating system: " + ss.getCP_COMMAND());

        bd_info.setDisable(true);
/*        bd_info.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.SECONDARY) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText("Look, a Confirmation Dialog");
                    alert.setContentText("Are you ok with this?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){

                        //bd_info.setDisable(false);
                        // ... user chose OK
                    } else {
                        // ... user chose CANCEL or closed the dialog
                    }
                    //cm.show(table, t.getScreenX(), t.getScreenY());
                }
            }
        });*/
        server_info.setDisable(true);
        mail_info.setDisable(true);

        Image img;
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

        AnchorPane.setTopAnchor(work_dir, 60d);
        AnchorPane.setLeftAnchor(work_dir, 300d);

        HelpContent.getChildren().clear();

        VBox vBox = new VBox();
        VBox.setVgrow(HelpContent, Priority.ALWAYS);
        VBox.setVgrow(bd_info, Priority.ALWAYS);
        VBox.setVgrow(server_info, Priority.ALWAYS);
        VBox.setVgrow(mail_info, Priority.ALWAYS);
        vBox.setPadding(new Insets(50,30,50,30));
        HelpContent.getChildren().add(name);
        HelpContent.getChildren().add(email);
        HelpContent.getChildren().add(company);
        HelpContent.getChildren().add(image);
        HelpContent.getChildren().add(work_dir);
        vBox.getChildren().add(HelpContent);
        vBox.getChildren().add(bd_info);
        vBox.getChildren().add(server_info);
        vBox.getChildren().add(mail_info);
        Content.getChildren().add(vBox);
    }
    @FXML
    protected void EmployeesOnClick(){
        Content.getChildren().clear();
        db.connect();
        LinkedHashMap<Department, ArrayList<Employee>> departments = db.getEmployees();
        db.close();
        VBox menuDeps = new VBox();
        Button addDep = new Button("Добавить");
        addDep.setMinWidth(200d);
        Accordion menu = new Accordion();
        addDep.setOnAction(actionEvent -> NewDepartmentModal.newWindow());
        for(Map.Entry<Department, ArrayList<Employee>> department : departments.entrySet()){
            VBox p_content = new VBox();
            p_content.setMinWidth(150d);

            TitledPane pane = new TitledPane(department.getKey().getName(), p_content);
            pane.setId("dep"+department.getKey().getId());
            Button addEmp = new Button("Добавить");
            addEmp.setMinWidth(p_content.getMinWidth());
            addEmp.setOnAction(actionEvent -> NewEmployeeModal.newWindow(department.getKey().getId()));
            p_content.getChildren().add(addEmp);

            for(Employee emp : department.getValue()){
                Button btn = new Button(emp.getName());
                btn.setId("emp"+emp.getId());
                btn.setMinWidth(p_content.getMinWidth());
                btn.setOnAction(actionEvent -> EmployeeInfoOnClick(btn.getId()));
                ContextMenu context = new ContextMenu();
                MenuItem itemDelete = new MenuItem("Удалить");
                itemDelete.setOnAction(actionEvent -> {
                    db.connect();
                    db.DeleteEmployee(Integer.parseInt(btn.getId().substring(3)));
                    db.close();
                });
                MenuItem itemRefactor = new MenuItem("Редактировать");
                itemRefactor.setOnAction(actionEvent -> {
                    RefactorEmployeeModal.newWindow();
                });
                context.getItems().add(itemDelete);
                context.getItems().add(itemRefactor);
                btn.setContextMenu(context);
               /* btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        if(t.getButton() == MouseButton.SECONDARY) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation Dialog");
                            alert.setHeaderText("Look, a Confirmation Dialog");
                            alert.setContentText("Are you ok with this?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK){

                                //bd_info.setDisable(false);
                                // ... user chose OK
                            } else {
                                // ... user chose CANCEL or closed the dialog
                            }
                            //cm.show(table, t.getScreenX(), t.getScreenY());
                        }
                    }
                });*/
                p_content.getChildren().add(btn);

            }
            ContextMenu context = new ContextMenu();
            MenuItem itemDelete = new MenuItem("Удалить");
            itemDelete.setOnAction(actionEvent -> {
                db.connect();
                db.DeleteDepartment(Integer.parseInt(pane.getId().substring(3)));
                db.close();
            });
            MenuItem itemRefactor = new MenuItem("Редактировать");
            itemRefactor.setOnAction(actionEvent -> {
                RefactorDepartmentModal.newWindow(Integer.parseInt(pane.getId().substring(3)));
            });
            context.getItems().add(itemDelete);
            context.getItems().add(itemRefactor);
            pane.setContextMenu(context);
            menu.getPanes().add(pane);
        }

        menuDeps.getChildren().add(addDep);
        menuDeps.getChildren().add(menu);
        Content.getChildren().add(menuDeps);
    }
    @FXML
    protected void MailingsOnClick(){
        Content.getChildren().clear();
        SplitPane split = new SplitPane();
        VBox right = new VBox();
        VBox left = new VBox();
        Label dep = new Label("Получатели: ");
        Label totalSent = new Label("Всего писем разослано: ");
        Label totalUsed = new Label("Всего по ссылкам перешло: ");
        TextArea logs = new TextArea();
        VBox.setVgrow(logs,Priority.ALWAYS);
        right.setPadding(new Insets(30,30,50,30));
        right.setSpacing(20);

        ScrollPane sp = new ScrollPane();
        sp.setContent(left);
        sp.setMaxWidth(200);

        ArrayList<Mailing> mailings;
        DB db = new DB();
        db.connect();
        mailings = db.getMailings();
        db.close();

        mailings.forEach(mailing -> {
                    Button btn = new Button();
                    btn.setId("mlng" + mailing.getId());
                    btn.setText("От: " + mailing.getTime());
                    btn.setOnAction(actionEvent -> {
                        ArrayList<LastMailing> lastMailings;

                        db.connect();
                        lastMailings = db.getLastMailing(Integer.parseInt(btn.getId().substring(4)));
                        Department department = db.getDepartment(mailing.getDep_id());
                        db.close();

                        dep.setText("Получатели: " + department.getName());
                        totalSent.setText("Всего писем разослано: " + mailing.getTotal_sent());
                        totalUsed.setText("Всего по ссылкам перешло: " + mailing.getTotal_used());
                        StringBuilder sb = new StringBuilder();
                        lastMailings.forEach(lastMailing ->
                                sb.append("Время использования: ").append(lastMailing.getTimeOfUse()).append(" Использованный ip: ").append(lastMailing.getUsedIp()).append("\n"));
                        logs.setText(sb.toString());
                    });

                        ContextMenu context = new ContextMenu();
                        MenuItem itemDelete = new MenuItem("Удалить");
                        itemDelete.setOnAction(actionEvent -> {
                            db.connect();
                            db.DeleteMailing(Integer.parseInt(btn.getId().substring(4)));
                            db.close();
                        });

                        context.getItems().add(itemDelete);

                        btn.setContextMenu(context);

                        left.getChildren().add(btn);

                });

        right.getChildren().add(dep);
        right.getChildren().add(totalSent);
        right.getChildren().add(totalUsed);
        right.getChildren().add(logs);


        split.setPrefSize(660, 560);
        left.setPrefSize(200, 560);
        left.setMaxWidth(200);
        right.setPrefSize(460, 560);

        HBox.setHgrow(split, Priority.ALWAYS);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        split.getItems().add(sp);
        split.getItems().add(right);
        Content.getChildren().add(split);
    }
    @FXML
    protected void StatisticOnClick(){
        Content.getChildren().clear();
        VBox main_vbox = new VBox();
        HBox first = new HBox();
        VBox lastMlngRaiting = new VBox();
        VBox companyRaiting = new VBox();
        VBox depsRaiting = new VBox();
        VBox recomends = new VBox();
        //ScatterChart third = new ScatterChart<>(Axis<x>, Axis<  >);
        Label lblLastMlngRait = new Label("Рейтинг последней рассылки: ");
        Label lblCompRait = new Label("Рейтинг компании: ");
        Label lblDepsRait = new Label("Рейтинги отделов: ");
        Label lblRecs = new Label("Рекомендации: ");
        Circle lstMlngCircle = new Circle();
        lstMlngCircle.setRadius(40d);
        lstMlngCircle.setFill(Color.LIGHTGRAY);
        Circle cmpnCircle = new Circle();
        cmpnCircle.setRadius(40d);
        cmpnCircle.setFill(Color.LIGHTGRAY);
        ScrollPane deps = new ScrollPane();
        VBox sp_deps = new VBox();
        ScrollPane recs = new ScrollPane();
        AnchorPane stat1 = new AnchorPane();
        Label score1 = new Label(String.valueOf(countLastMailingRaiting()));
        score1.setFont(new Font("Arial", 20));
        AnchorPane stat2 = new AnchorPane();
        Label score2 = new Label(String.valueOf(countCompanyRaiting()));
        score2.setFont(new Font("Arial", 20));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Рассылка");

        NumberAxis yAxis = new NumberAxis();

        yAxis.setLabel("Рейтинг");
        BarChart<Number, String> second = new BarChart<>(yAxis, xAxis);

        XYChart.Series<Number, String> dataSeries1 = new XYChart.Series<>();
        dataSeries1.setName("Рейтинг");

        HashMap<String, Integer> mailingsRait = countMailingsRaiting();
        mailingsRait.forEach((date, rait)-> dataSeries1.getData().add(new XYChart.Data<>(rait, date)));
        second.getData().add(dataSeries1);

        second.setTitle("Рейтинг рассылок в динамике");

        AnchorPane.setTopAnchor(lstMlngCircle, 40d);
        AnchorPane.setLeftAnchor(lstMlngCircle, 40d);
        AnchorPane.setTopAnchor(score1, 70d);
        AnchorPane.setLeftAnchor(score1, 75d);
        AnchorPane.setTopAnchor(cmpnCircle, 40d);
        AnchorPane.setLeftAnchor(cmpnCircle, 40d);
        AnchorPane.setTopAnchor(score2, 70d);
        AnchorPane.setLeftAnchor(score2, 75d);

        int tmp = Integer.parseInt(score1.getText());
        if(tmp == -1) {
            lstMlngCircle.setFill(Color.LIGHTGRAY);
            AnchorPane.setTopAnchor(score1, 70d);
            AnchorPane.setLeftAnchor(score1, 70d);
        }else if(tmp  < 5){
            lstMlngCircle.setFill(Color.LIGHTCORAL);
        }else if(tmp > 8) {
            lstMlngCircle.setFill(Color.LIGHTGREEN);
        }else{
            lstMlngCircle.setFill(Color.DARKORANGE);
        }

        tmp = Integer.parseInt(score2.getText());
        if(tmp == -1) {
            cmpnCircle.setFill(Color.LIGHTGRAY);
            AnchorPane.setTopAnchor(score2, 70d);
            AnchorPane.setLeftAnchor(score2, 70d);
        }else if(tmp  < 5){
            cmpnCircle.setFill(Color.LIGHTCORAL);
        }else if(tmp > 8) {
            cmpnCircle.setFill(Color.LIGHTGREEN);
        }else{
            cmpnCircle.setFill(Color.DARKORANGE);
        }

        HBox.setHgrow(main_vbox, Priority.ALWAYS);
        VBox.setVgrow(first, Priority.ALWAYS);
        VBox.setVgrow(second, Priority.ALWAYS);
        HBox.setHgrow(lastMlngRaiting, Priority.ALWAYS);
        HBox.setHgrow(companyRaiting, Priority.ALWAYS);
        HBox.setHgrow(depsRaiting, Priority.ALWAYS);
        HBox.setHgrow(recomends, Priority.ALWAYS);
        VBox.setVgrow(lblLastMlngRait, Priority.ALWAYS);
        VBox.setVgrow(stat1, Priority.ALWAYS);
        VBox.setVgrow(lblCompRait, Priority.ALWAYS);
        VBox.setVgrow(stat2, Priority.ALWAYS);


        stat1.getChildren().add(lstMlngCircle);
        stat1.getChildren().add(score1);

        stat2.getChildren().add(cmpnCircle);
        stat2.getChildren().add(score2);

        stat1.setPrefSize(90, 90);
        stat2.setPrefSize(90, 90);
        lastMlngRaiting.getChildren().add(lblLastMlngRait);
        lastMlngRaiting.getChildren().add(stat1);

        companyRaiting.getChildren().add(lblCompRait);
        companyRaiting.getChildren().add(stat2);

        HashMap<String, Integer> depsMap = countDepsRaiting();
        depsMap.forEach((dep, rait)->{
            Label tmp_l = new Label(dep+": "+rait);
            sp_deps.getChildren().add(tmp_l);
        });
        deps.setContent(sp_deps);

        depsRaiting.getChildren().add(lblDepsRait);
        depsRaiting.getChildren().add(deps);

        recomends.getChildren().add(lblRecs);
        recomends.getChildren().add(recs);

        first.setSpacing(20);
        first.getChildren().add(lastMlngRaiting);
        first.getChildren().add(companyRaiting);
        first.getChildren().add(depsRaiting);
        first.getChildren().add(recomends);

        main_vbox.setSpacing(40);
        main_vbox.setPadding(new Insets(50,30,50,30));
        main_vbox.getChildren().add(first);
        main_vbox.getChildren().add(second);

        Content.getChildren().add(main_vbox);

    }
    @FXML
    protected void NewMailingOnClick(){
        Content.getChildren().clear();

        TextArea mail_text = new TextArea();

        mail_text.setPrefSize(305d, 420d);

        VBox mail_settings = new VBox();
        VBox mail_content = new VBox();

        VBox.setVgrow(mail_text, Priority.ALWAYS);
        HBox.setHgrow(mail_content, Priority.ALWAYS);

        mail_content.setMaxWidth(700);
        mail_text.setMaxSize(500, 650);
        mail_content.setSpacing(20);
        mail_settings.setSpacing(5);
        mail_content.setPadding(new Insets(50, 30, 50, 30));
        mail_settings.setPadding(new Insets(50, 30, 50, 30));


        Label label0 = new Label("Тема письма:");
        Label label1 = new Label("Текст письма:");
        Label label2 = new Label("Вставьте гиперссылку в\nформате HTML-тега,\nчто бы прикрепить к сообщению\nссылку на скачивание\nдроппера-документа или\nна регистрацию\nперехода сотрудника.");
        label2.setTextFill(Color.GRAY);
        Label label3 = new Label("Выберите участников рассылки:");
        Label label4 = new Label("Полезная нагрузка для рассылки:");
        Label label5 = new Label("Гиперссылка:");
        Label label6 = new Label("INFO");
        label6.setTextFill(Color.DARKRED);
        label6.setVisible(false);
        Button btn0 = new Button("Начать рассылку");
        TextField field1 = new TextField();//theme
        VBox.setVgrow(field1, Priority.NEVER);
        HBox.setHgrow(field1, Priority.NEVER);
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


        mail_content.getChildren().add(label1);
        mail_content.getChildren().add(mail_text);

        mail_settings.getChildren().add(label0);
        mail_settings.getChildren().add(field1);
        mail_settings.getChildren().add(label2);
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

            if (Validation.isNullOrEmpty(text) || Validation.isNullOrEmpty(recipients)) {
                label6.setVisible(true);
                label6.setText("Заполните необходимые поля");
                return;
            }

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


        Content.getChildren().add(mail_content);
        Content.getChildren().add(mail_settings);
    }
    protected void EmployeeInfoOnClick(String id){
        Content.getChildren().remove(HelpContent);
        HelpContent.getChildren().clear();
        AnchorPane.setRightAnchor(HelpContent,40d);
        AnchorPane.setLeftAnchor(HelpContent, 180d);

            db.connect();
            Employee emp = db.getEmployee(id.substring(3));
            db.close();
            Image img;
        img = new Image(Objects.requireNonNull(StartApplication.class.getResourceAsStream("default_person.jpg")));

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
            AnchorPane.setLeftAnchor(circle, 350d);
            Label rait = new Label(Integer.toString(emp.getRaiting()));
            rait.setFont(new Font("Arial", 20));
            AnchorPane.setTopAnchor(rait, 90d);
            AnchorPane.setLeftAnchor(rait, 385d);
            if(emp.getRaiting() == -1) {
                circle.setFill(Color.LIGHTGRAY);
                AnchorPane.setTopAnchor(rait, 90d);
                AnchorPane.setLeftAnchor(rait, 390d);
            }else if(emp.getRaiting() < 5){
                circle.setFill(Color.LIGHTCORAL);
            }else if(emp.getRaiting() > 8) {
                circle.setFill(Color.LIGHTGREEN);
            }else{
                circle.setFill(Color.DARKORANGE);
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

//TODO:
// 1. регистрация пользователя
// 2. рейтинг внутри круга смещен
// 3. настроить нормальный размер кнопки внутри аккордеона 