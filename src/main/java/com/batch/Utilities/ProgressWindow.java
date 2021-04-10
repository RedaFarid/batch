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
        label.setText(Title);
        graphicsBuilder();
    }

    private void graphicsBuilder() {
        work.setPrefSize(400, 100);

        progressbar.setProgress(0);
        progressbar.setPrefWidth(400);
        progressbar.setPrefHeight(50);

        progressindicator.setProgress(0);
        progressindicator.setPrefSize(50, 50);

        progressindicator.progressProperty().bind(progressbar.progressProperty());
        hbox.getChildren().addAll(progressbar, progressindicator);

        StringProgress.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                work.setText(newValue);
            }
        });

        Pane.setTop(label);
        Pane.setCenter(work);
        Pane.setBottom(hbox);
        Pane.setPadding(new Insets(10));

        Scene scene = new Scene(Pane);

        dialogStage.initOwner(Window);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(scene);
        dialogStage.setTitle("Progress .... ");
        dialogStage.setOnCloseRequest(a -> {
            a.consume();
        });
    }

    public DoubleProperty GetProgress() {
        return progressbar.progressProperty();
    }

    public StringProperty GetStringProgress() {
        return StringProgress;
    }

    public void show() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialogStage.show();
            }
        });

    }

    public void close() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialogStage.close();
            }
        });
    }

}
