

package com.batch.GUI.InitialWindow.SubWindows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelpWindow extends Stage {
    public HelpWindow() {
        VBox vBox = new VBox();
        Label label = new Label("Mixing platform V2.1\nSystem by CIRCLE\n");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, (double)15.0F));
        label.setPadding(new Insets((double)10.0F));
        label.prefWidthProperty().bind(this.widthProperty());
        vBox.getChildren().add(label);
        vBox.setPadding(new Insets((double)20.0F));
        Scene scene = new Scene(vBox);
        this.setScene(scene);
        this.setTitle("About system");
        this.setWidth((double)500.0F);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.UTILITY);
    }
}
