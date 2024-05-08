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

public class IPC_Fill_From_Tank_3_Message extends Stage {

    private static IPC_Fill_From_Tank_3_Message singleton = null;
    private Map<String, RowDataDefinition> allDataDefinitions;
    private Stage mainWindow;
    
    private BorderPane root = new BorderPane();
    private Pane pane = new  Pane();
    private HBox hbox = new HBox();

    private IPC_Fill_From_Tank_3_Message(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        this.mainWindow = stage;
        this.allDataDefinitions = allDataDefinitions;
        initialization();

    }

    public static Stage getWindow(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        if (singleton == null) {
            synchronized (IPC_Fill_From_Tank_3_Message.class) {
                singleton = new IPC_Fill_From_Tank_3_Message(stage, allDataDefinitions);
            }
        }
        return singleton;
    }

    private void initialization() {

        Label label = new Label("Tank - 3 manual add phase started");
        label.prefWidthProperty().bind(root.widthProperty());
        label.setPadding(new Insets(10));
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;");
        
        
        
        Label content = new Label("Kindly add IPC container and insert flexible hose inside it from V27,\nAfter adjusting setup please press [CONFIRMED] ");
        content.setStyle("-fx-text-fill:white;");
        content.prefWidthProperty().bind(pane.widthProperty());
        content.prefHeightProperty().bind(pane.heightProperty());
        content.setPadding(new Insets(10));
        
        Button ok = new Button("CONFIRMED");
        ok.setPrefWidth(250);
        ok.setOnMouseClicked(action -> {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_3_Message_Confirmation)).setValue(true);
            hide();
        });
        
        
        hbox.getChildren().add(ok);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
        
        
        pane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.getChildren().addAll(content);
        
        root.setTop(label);
        root.setCenter(pane);
        root.setBottom(hbox);
        root.setPadding(new Insets(5));
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");
        
        setScene(scene);
        setTitle("Tank 3 IPC container filling");
        setWidth(400);
        setHeight(200);
        initOwner(mainWindow);
        initStyle(StageStyle.UTILITY);
        setResizable(false);
        setOnCloseRequest(Event::consume);
    }
}
