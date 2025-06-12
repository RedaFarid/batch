
package com.batch.Utilities;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
    private Stage Window = null;
    private Stage dialogStage = new Stage();
    private ProgressBar progressbar = new ProgressBar();
    private ProgressIndicator progressindicator = new ProgressIndicator();
    private Label label = new Label();
    private Label work = new Label();
    private BorderPane Pane = new BorderPane();
    private HBox hbox = new HBox();
    private StringProperty StringProgress = new SimpleStringProperty("Calculating ...");

    public ProgressWindow(Stage parentsatge, String Title) {
        this.Window = parentsatge;
        this.label.setText(Title);
        this.graphicsBuilder();
    }

    private void graphicsBuilder() {
        this.work.setPrefSize((double)400.0F, (double)100.0F);
        this.progressbar.setProgress((double)0.0F);
        this.progressbar.setPrefWidth((double)400.0F);
        this.progressbar.setPrefHeight((double)50.0F);
        this.progressindicator.setProgress((double)0.0F);
        this.progressindicator.setPrefSize((double)50.0F, (double)50.0F);
        this.progressindicator.progressProperty().bind(this.progressbar.progressProperty());
        this.hbox.getChildren().addAll(new Node[]{this.progressbar, this.progressindicator});
        this.StringProgress.addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ProgressWindow.this.work.setText(newValue);
            }
        });
        this.Pane.setTop(this.label);
        this.Pane.setCenter(this.work);
        this.Pane.setBottom(this.hbox);
        this.Pane.setPadding(new Insets((double)10.0F));
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
