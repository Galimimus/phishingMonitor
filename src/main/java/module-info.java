module com.galimimus.phishingmonitor {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.galimimus.phishingmonitor to javafx.fxml;
    exports com.galimimus.phishingmonitor;
}