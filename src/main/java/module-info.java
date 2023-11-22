module com.galimimus.phishingmonitor {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.java;
    requires java.sql;
    requires java.naming;


    opens com.galimimus.phishingmonitor to javafx.fxml;
    exports com.galimimus.phishingmonitor;
    exports com.galimimus.phishingmonitor.controllers;
    opens com.galimimus.phishingmonitor.controllers to javafx.fxml;
}