package com.batch.GUI.Alarms;

import com.batch.ApplicationContext;
import com.batch.GUI.FacePlates.FaceplateTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Utilities extends Stage {

    private static volatile Utilities singleton = null;
    private Stage mainWindow;

    private BorderPane layout = new BorderPane();
    private GridPane gridPane = new GridPane();

    private Label label = new Label("Utilities");

    private Label hiPressLimit = new Label("HI pressure limit (Bar)");
    private Label loPressLimit = new Label("Lo pressure limit (Bar)");

    private FaceplateTextField hiPressureField = new FaceplateTextField();
    private FaceplateTextField loPressureField = new FaceplateTextField();

    private final AlarmsController controller;
    private final AlarmsModel model;

    private Utilities(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = ApplicationContext.applicationContext.getBean(AlarmsController.class);
        this.model = controller.getModel();
        initialization();
        actions();
    }

    public static Utilities getUtilitiesWindow(Stage mainWindow) {
        if (singleton == null) {
            synchronized (Utilities.class) {
                singleton = new Utilities(mainWindow);
            }
        }
        return singleton;
    }

    private void initialization() {

        label.prefWidthProperty().bind(layout.widthProperty());
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:48;");
        label.setPrefHeight(100);

        hiPressLimit.setStyle("-fx-text-fill:white;");
        loPressLimit.setStyle("-fx-text-fill:white;");

        hiPressureField.setPrefWidth(250);
        loPressureField.setPrefWidth(250);
        
        hiPressureField.textProperty().bindBidirectional(model.getAirPressureHiAlarm());
        loPressureField.textProperty().bindBidirectional(model.getAirPressureLoAlarm());

        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(20));
        gridPane.add(hiPressLimit, 1, 1);
        gridPane.add(loPressLimit, 1, 2);

        gridPane.add(hiPressureField, 2, 1);
        gridPane.add(loPressureField, 2, 2);

        layout.setTop(label);
        layout.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, CornerRadii.EMPTY, new Insets(2))));
        layout.setCenter(gridPane);
        
        setTitle("Air-pressure settings");
        initOwner(mainWindow);
        initStyle(StageStyle.UTILITY);
        setScene(new Scene(layout));
        setHeight(300);
        setWidth(500);
        setResizable(false);
        
    }

    private void actions() {
        hiPressureField.onEneterKeyPressed((String param) -> {
            if (hiPressureField.getText().length() > 0) {
                controller.highPressureLimitCommit();
            }
            return null;
        });
        loPressureField.onEneterKeyPressed((String param) -> {
            if (hiPressureField.getText().length() > 0) {
                controller.lowPressureLimitCommit();
            }
            return null;
        });
    }
}
