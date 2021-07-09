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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Map;

public class Mixer_2_Manual_Add_Message extends Stage {

    private static Mixer_2_Manual_Add_Message singleton = null;
    private Map<String, RowDataDefinition> allDataDefinitions;
    private Stage mainWindow;
    
    private BorderPane root = new BorderPane();
    private Pane pane = new  Pane();
    private HBox hbox = new HBox();

    private Mixer_2_Manual_Add_Message(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        this.mainWindow = stage;
        this.allDataDefinitions = allDataDefinitions;
        initialization();

    }

    public static Stage getWindow(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        if (singleton == null) {
            synchronized (Mixer_2_Manual_Add_Message.class) {
                singleton = new Mixer_2_Manual_Add_Message(stage, allDataDefinitions);
            }
        }
        return singleton;
    }

    private void initialization() {

        Label label = new Label("Mixer - 2 manual add phase started");
        label.prefWidthProperty().bind(root.widthProperty());
        label.setPadding(new Insets(10));
        
        
        
        
        Label content = new Label("Kindly supervise manual add opertion,\nAfter manual add operation completion please press [CONFIRMED] ");
        content.prefWidthProperty().bind(pane.widthProperty());
        content.prefHeightProperty().bind(pane.heightProperty());
        content.setPadding(new Insets(10));
        
        Button ok = new Button("CONFIRMED");
        ok.setPrefWidth(250);
        ok.setOnMouseClicked(action -> {
            ((BooleanDataType) allDataDefinitions.get("General").getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(true);
            hide();
        });
        
        
        hbox.getChildren().add(ok);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
        
        
        pane.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.getChildren().addAll(content);
        
        root.setTop(label);
        root.setCenter(pane);
        root.setBottom(hbox);
        root.setPadding(new Insets(5));
        root.setBackground(new Background(new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");
        
        setScene(scene);
        setTitle("Mixer 2 manual add confirmation message");
        setWidth(400);
        setHeight(200);
        initOwner(mainWindow);
        initStyle(StageStyle.UTILITY);
        setResizable(false);
        setOnCloseRequest(Event::consume);
    }
}
