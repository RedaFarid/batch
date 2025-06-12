package com.batch.GUI.InitialWindow.SubWindows;

import com.batch.PLCDataSource.PLC.ComplexDataType.GeneralOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Map;

public class IPC_Fill_From_Tank_2_Message extends Stage {
    private static IPC_Fill_From_Tank_2_Message singleton = null;
    private final Map<String, RowDataDefinition> allDataDefinitions;
    private final Stage mainWindow;
    private final BorderPane root = new BorderPane();
    private final Pane pane = new Pane();
    private final HBox hbox = new HBox();

    private IPC_Fill_From_Tank_2_Message(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        this.mainWindow = stage;
        this.allDataDefinitions = allDataDefinitions;
        this.initialization();
    }

    public static Stage getWindow(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        if (singleton == null) {
            synchronized (IPC_Fill_From_Tank_2_Message.class) {
                singleton = new IPC_Fill_From_Tank_2_Message(stage, allDataDefinitions);
            }
        }

        return singleton;
    }

    private void initialization() {
        Label label = new Label("Tank - 2 feeding IPC container phase started");
        label.prefWidthProperty().bind(this.root.widthProperty());
        label.setPadding(new Insets(10.0F));
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;");
        Label content = new Label("Kindly add IPC container and insert flexible hose inside it from V15,\nAfter adjusting setup please press [CONFIRMED] ");
        content.setStyle("-fx-text-fill:white;");
        content.prefWidthProperty().bind(this.pane.widthProperty());
        content.prefHeightProperty().bind(this.pane.heightProperty());
        content.setPadding(new Insets(10.0F));
        Button ok = new Button("CONFIRMED");
        ok.setPrefWidth(250.0F);
        ok.setOnMouseClicked((action) -> {
            ((BooleanDataType) this.allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_2_Message_Confirmation)).setValue(true);
            this.hide();
        });
        this.hbox.getChildren().add(ok);
        this.hbox.setAlignment(Pos.CENTER);
        this.hbox.setPadding(new Insets(10.0F));
        this.pane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.pane.getChildren().addAll(content);
        this.root.setTop(label);
        this.root.setCenter(this.pane);
        this.root.setBottom(this.hbox);
        this.root.setPadding(new Insets(5.0F));
        this.root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(this.root);
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");
        this.setScene(scene);
        this.setTitle("Tank 2 IPC container filling");
        this.setWidth(400.0F);
        this.setHeight(200.0F);
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
        this.setOnCloseRequest(Event::consume);
    }
}
