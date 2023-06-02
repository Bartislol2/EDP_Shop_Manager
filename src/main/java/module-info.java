module pl.gawryszewski.edp_projekt {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.common;
    requires com.google.gson;
    requires org.apache.commons.io;


    //opens pl.gawryszewski.edp_projekt to javafx.fxml;
    //exports pl.gawryszewski.edp_projekt;
    exports pl.gawryszewski.edp_projekt.controller;
    opens pl.gawryszewski.edp_projekt.controller to javafx.fxml;
    exports pl.gawryszewski.edp_projekt.application;
    opens pl.gawryszewski.edp_projekt.application to javafx.fxml;
    exports pl.gawryszewski.edp_projekt.model;
    opens pl.gawryszewski.edp_projekt.model to javafx.fxml;
}