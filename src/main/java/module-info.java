module com.galimimus.phishingmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.mail;
    requires activation;
    requires java.naming;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires mysql.connector.j;
    requires jdk.httpserver;
    requires org.yaml.snakeyaml;
    requires static lombok;


    opens com.galimimus.phishingmonitor to javafx.fxml;
    exports com.galimimus.phishingmonitor;
    exports com.galimimus.phishingmonitor.controllers;
    opens com.galimimus.phishingmonitor.controllers to javafx.fxml;
}