package com.batch.GUI.InitialWindow.SubWindows;

import com.batch.PLCDataSource.PLC.ComplexDataType.GeneralOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Map;

public class Mixer_1_Manual_Add_Message extends Stage {
    private static Mixer_1_Manual_Add_Message singleton = null;
    private final Map<String, RowDataDefinition> allDataDefinitions;
    private final Stage mainWindow;
    private final BorderPane root = new BorderPane();
    private final Pane pane = new Pane();
    private final HBox hbox = new HBox();

    private Mixer_1_Manual_Add_Message(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        this.mainWindow = stage;
        this.allDataDefinitions = allDataDefinitions;
        this.initialization();
    }

    public static Mixer_1_Manual_Add_Message getWindow(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        if (singleton == null) {
            synchronized (Mixer_1_Manual_Add_Message.class) {
                singleton = new Mixer_1_Manual_Add_Message(stage, allDataDefinitions);
            }
        }

        return singleton;
    }

    private void initialization() {
        Label label = new Label("Mixer - 1 manual add phase started");
        label.prefWidthProperty().bind(this.root.widthProperty());
        label.setPadding(new Insets(10.0F));
        Label content = new Label("Kindly supervise manual add opertion,\nAfter manual add operation completion please press [CONFIRMED] ");
        content.prefWidthProperty().bind(this.pane.widthProperty());
        content.prefHeightProperty().bind(this.pane.heightProperty());
        content.setPadding(new Insets(10.0F));
        Button ok = new Button("CONFIRMED");
        ok.setPrefWidth(250.0F);
        ok.setOnMouseClicked((action) -> {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_1_Manual_Add_Confirmation)).setValue(true);
            this.hide();
        });
        this.hbox.getChildren().add(ok);
        this.hbox.setAlignment(Pos.CENTER);
        this.hbox.setPadding(new Insets(10.0F));
        this.pane.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.pane.getChildren().addAll(content);
        this.root.setTop(label);
        this.root.setCenter(this.pane);
        this.root.setBottom(this.hbox);
        this.root.setPadding(new Insets(5.0F));
        this.root.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(this.root);
        scene.getStylesheets().add("Styles/Faceplate.css");
        this.setScene(scene);
        this.setTitle("Mixer 1 manual add confirmation message");
        this.setWidth(400.0F);
        this.setHeight(200.0F);
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
        this.setOnCloseRequest(Event::consume);
    }
}
