package com.batch.Utilities;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressWindow {
    private final Stage dialogStage = new Stage();
    private final ProgressBar progressbar = new ProgressBar();
    private final ProgressIndicator progressindicator = new ProgressIndicator();
    private final Label label = new Label();
    private final Label work = new Label();
    private final BorderPane Pane = new BorderPane();
    private final HBox hbox = new HBox();
    private final StringProperty StringProgress = new SimpleStringProperty("Calculating ...");
    private Stage Window = null;

    public ProgressWindow(Stage parentsatge, String Title) {
        this.Window = parentsatge;
        this.label.setText(Title);
        this.graphicsBuilder();
    }

    private void graphicsBuilder() {
        this.work.setPrefSize(400.0F, 100.0F);
        this.progressbar.setProgress(0.0F);
        this.progressbar.setPrefWidth(400.0F);
        this.progressbar.setPrefHeight(50.0F);
        this.progressindicator.setProgress(0.0F);
        this.progressindicator.setPrefSize(50.0F, 50.0F);
        this.progressindicator.progressProperty().bind(this.progressbar.progressProperty());
        this.hbox.getChildren().addAll(this.progressbar, this.progressindicator);
        this.StringProgress.addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ProgressWindow.this.work.setText(newValue);
            }
        });
        this.Pane.setTop(this.label);
        this.Pane.setCenter(this.work);
        this.Pane.setBottom(this.hbox);
        this.Pane.setPadding(new Insets(10.0F));
        Scene scene = new Scene(this.Pane);
        this.dialogStage.initOwner(this.Window);
        this.dialogStage.initStyle(StageStyle.UTILITY);
        this.dialogStage.setResizable(false);
        this.dialogStage.initModality(Modality.APPLICATION_MODAL);
        this.dialogStage.setScene(scene);
        this.dialogStage.setTitle("Progress .... ");
        this.dialogStage.setOnCloseRequest((a) -> a.consume());
    }

    public DoubleProperty GetProgress() {
        return this.progressbar.progressProperty();
    }

    public StringProperty GetStringProgress() {
        return this.StringProgress;
    }

    public void show() {
        Platform.runLater(new Runnable() {
            public void run() {
                ProgressWindow.this.dialogStage.show();
            }
        });
    }

    public void close() {
        Platform.runLater(new Runnable() {
            public void run() {
                ProgressWindow.this.dialogStage.close();
            }
        });
    }
}
