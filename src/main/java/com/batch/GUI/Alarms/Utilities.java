
package com.batch.GUI.Alarms;

import com.batch.ApplicationContext;
import com.batch.GUI.FacePlates.FaceplateTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Utilities extends Stage {
    private static volatile Utilities singleton = null;
    private final Stage mainWindow;
    private final BorderPane layout = new BorderPane();
    private final GridPane gridPane = new GridPane();
    private final Label label = new Label("Air pressure settings");
    private final Label hiPressLimit = new Label("HI pressure limit (Bar)");
    private final Label loPressLimit = new Label("Lo pressure limit (Bar)");
    private final FaceplateTextField hiPressureField = new FaceplateTextField();
    private final FaceplateTextField loPressureField = new FaceplateTextField();
    private final AlarmsController controller;
    private final AlarmsModel model;

    private Utilities(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = (AlarmsController)ApplicationContext.applicationContext.getBean(AlarmsController.class);
        this.model = this.controller.getModel();
        this.initialization();
        this.actions();
    }

    public static Utilities getUtilitiesWindow(Stage mainWindow) {
        if (singleton == null) {
            synchronized(Utilities.class) {
                singleton = new Utilities(mainWindow);
            }
        }

        return singleton;
    }

    private void initialization() {
        this.label.prefWidthProperty().bind(this.layout.widthProperty());
        this.label.setAlignment(Pos.CENTER);
        this.label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:36;");
        this.label.setPrefHeight((double)100.0F);
        this.hiPressLimit.setStyle("-fx-text-fill:white;");
        this.loPressLimit.setStyle("-fx-text-fill:white;");
        this.hiPressureField.setPrefWidth((double)250.0F);
        this.loPressureField.setPrefWidth((double)250.0F);
        this.hiPressureField.textProperty().bindBidirectional(this.model.getAirPressureHiAlarm());
        this.loPressureField.textProperty().bindBidirectional(this.model.getAirPressureLoAlarm());
        this.gridPane.setVgap((double)10.0F);
        this.gridPane.setHgap((double)10.0F);
        this.gridPane.setPadding(new Insets((double)20.0F));
        this.gridPane.add(this.hiPressLimit, 1, 1);
        this.gridPane.add(this.loPressLimit, 1, 2);
        this.gridPane.add(this.hiPressureField, 2, 1);
        this.gridPane.add(this.loPressureField, 2, 2);
        this.layout.setTop(this.label);
        this.layout.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.DARKBLUE, CornerRadii.EMPTY, new Insets((double)2.0F))}));
        this.layout.setCenter(this.gridPane);
        this.setTitle("Air-pressure settings");
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.setScene(new Scene(this.layout));
        this.setHeight((double)300.0F);
        this.setWidth((double)500.0F);
        this.setResizable(false);
    }

    private void actions() {
        this.hiPressureField.onEneterKeyPressed((param) -> {
            if (this.hiPressureField.getText().length() > 0) {
                this.controller.highPressureLimitCommit();
            }

            return null;
        });
        this.loPressureField.onEneterKeyPressed((param) -> {
            if (this.hiPressureField.getText().length() > 0) {
                this.controller.lowPressureLimitCommit();
            }

            return null;
        });
    }
}
