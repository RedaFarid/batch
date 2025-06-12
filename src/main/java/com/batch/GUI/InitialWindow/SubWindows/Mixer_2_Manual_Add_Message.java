
package com.batch.GUI.InitialWindow.SubWindows;

import com.batch.PLCDataSource.PLC.ComplexDataType.GeneralOutput;
import com.batch.PLCDataSource.PLC.ComplexDataType.RowDataDefinition;
import com.batch.PLCDataSource.PLC.ElementaryDefinitions.BooleanDataType;
import java.util.Map;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class Mixer_2_Manual_Add_Message extends Stage {
    private static Mixer_2_Manual_Add_Message singleton = null;
    private Map<String, RowDataDefinition> allDataDefinitions;
    private Stage mainWindow;
    private BorderPane root = new BorderPane();
    private Pane pane = new Pane();
    private HBox hbox = new HBox();

    private Mixer_2_Manual_Add_Message(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        this.mainWindow = stage;
        this.allDataDefinitions = allDataDefinitions;
        this.initialization();
    }

    public static Stage getWindow(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        if (singleton == null) {
            synchronized(Mixer_2_Manual_Add_Message.class) {
                singleton = new Mixer_2_Manual_Add_Message(stage, allDataDefinitions);
            }
        }

        return singleton;
    }

    private void initialization() {
        Label label = new Label("Mixer - 2 manual add phase started");
        label.prefWidthProperty().bind(this.root.widthProperty());
        label.setPadding(new Insets((double)10.0F));
        Label content = new Label("Kindly supervise manual add opertion,\nAfter manual add operation completion please press [CONFIRMED] ");
        content.prefWidthProperty().bind(this.pane.widthProperty());
        content.prefHeightProperty().bind(this.pane.heightProperty());
        content.setPadding(new Insets((double)10.0F));
        Button ok = new Button("CONFIRMED");
        ok.setPrefWidth((double)250.0F);
        ok.setOnMouseClicked((action) -> {
            ((BooleanDataType)((RowDataDefinition)this.allDataDefinitions.get("General")).getAllValues().get(GeneralOutput.Mixer_2_Manual_Add_Confirmation)).setValue(true);
            this.hide();
        });
        this.hbox.getChildren().add(ok);
        this.hbox.setAlignment(Pos.CENTER);
        this.hbox.setPadding(new Insets((double)10.0F));
        this.pane.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY)}));
        this.pane.getChildren().addAll(new Node[]{content});
        this.root.setTop(label);
        this.root.setCenter(this.pane);
        this.root.setBottom(this.hbox);
        this.root.setPadding(new Insets((double)5.0F));
        this.root.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHEAT, CornerRadii.EMPTY, Insets.EMPTY)}));
        Scene scene = new Scene(this.root);
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");
        this.setScene(scene);
        this.setTitle("Mixer 2 manual add confirmation message");
        this.setWidth((double)400.0F);
        this.setHeight((double)200.0F);
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
        this.setOnCloseRequest(Event::consume);
    }
}
