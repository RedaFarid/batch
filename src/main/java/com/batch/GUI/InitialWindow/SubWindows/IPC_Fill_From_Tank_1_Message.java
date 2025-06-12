

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

public class IPC_Fill_From_Tank_1_Message extends Stage {
    private static IPC_Fill_From_Tank_1_Message singleton = null;
    private Map<String, RowDataDefinition> allDataDefinitions;
    private Stage mainWindow;
    private BorderPane root = new BorderPane();
    private Pane pane = new Pane();
    private HBox hbox = new HBox();

    private IPC_Fill_From_Tank_1_Message(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        this.mainWindow = stage;
        this.allDataDefinitions = allDataDefinitions;
        this.initialization();
    }

    public static Stage getWindow(Stage stage, Map<String, RowDataDefinition> allDataDefinitions) {
        if (singleton == null) {
            synchronized(IPC_Fill_From_Tank_1_Message.class) {
                singleton = new IPC_Fill_From_Tank_1_Message(stage, allDataDefinitions);
            }
        }

        return singleton;
    }

    private void initialization() {
        Label label = new Label("Tank - 1 feeding IPC container phase started");
        label.prefWidthProperty().bind(this.root.widthProperty());
        label.setPadding(new Insets((double)10.0F));
        label.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;");
        Label content = new Label("Kindly add IPC container and insert flexible hose inside it from V15,\nAfter adjusting setup please press [CONFIRMED] ");
        content.setStyle("-fx-text-fill:white;");
        content.prefWidthProperty().bind(this.pane.widthProperty());
        content.prefHeightProperty().bind(this.pane.heightProperty());
        content.setPadding(new Insets((double)10.0F));
        Button ok = new Button("CONFIRMED");
        ok.setPrefWidth((double)250.0F);
        ok.setOnMouseClicked((action) -> {
            ((BooleanDataType)((RowDataDefinition)this.allDataDefinitions.get("General")).getAllValues().get(GeneralOutput.IPC_Fill_From_Tank_1_Message_Confirmation)).setValue(true);
            this.hide();
        });
        this.hbox.getChildren().add(ok);
        this.hbox.setAlignment(Pos.CENTER);
        this.hbox.setPadding(new Insets((double)10.0F));
        this.pane.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.CADETBLUE, CornerRadii.EMPTY, Insets.EMPTY)}));
        this.pane.getChildren().addAll(new Node[]{content});
        this.root.setTop(label);
        this.root.setCenter(this.pane);
        this.root.setBottom(this.hbox);
        this.root.setPadding(new Insets((double)5.0F));
        this.root.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.LIGHTBLUE.darker(), CornerRadii.EMPTY, Insets.EMPTY)}));
        Scene scene = new Scene(this.root);
        scene.getStylesheets().add("/GUI/Styles/Faceplate.css");
        this.setScene(scene);
        this.setTitle("Tank 1 IPC container filling");
        this.setWidth((double)400.0F);
        this.setHeight((double)200.0F);
        this.initOwner(this.mainWindow);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
        this.setOnCloseRequest(Event::consume);
    }
}
