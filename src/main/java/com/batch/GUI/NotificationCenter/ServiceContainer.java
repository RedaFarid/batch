
package com.batch.GUI.NotificationCenter;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ServiceContainer extends BorderPane {
    private final Map<String, BorderPane> allNotifications = new HashMap();
    private final String name;
    private VBox container;
    private Label header;
    private VBox pane;
    private HBox shifter;

    public ServiceContainer(String name) {
        this.name = name;
        this.initialize();
        this.createWindowAuthorities();
        this.graphicsBuild();
        this.actionHandling();
    }

    private void initialize() {
        this.container = new VBox();
        this.header = new Label(this.name.toUpperCase());
        this.pane = new VBox();
        this.shifter = new HBox(new Node[]{this.pane, this.container});
    }

    private void createWindowAuthorities() {
    }

    private void actionHandling() {
        this.header.setOnMouseClicked((action) -> {
            if (action.getClickCount() == 1) {
                if (this.getCenter() == null) {
                    this.setCenter(this.shifter);
                } else {
                    this.setCenter((Node)null);
                }
            }

        });
    }

    private void graphicsBuild() {
        this.header.prefWidthProperty().bind(this.widthProperty());
        this.header.setAlignment(Pos.CENTER);
        this.header.setPrefHeight((double)30.0F);
        this.header.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:18;-fx-font-family: 'Times New Roman';");
        this.header.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.ORANGERED, new CornerRadii((double)5.0F, (double)5.0F, (double)0.0F, (double)0.0F, false), Insets.EMPTY)}));
        this.header.setOnMouseEntered((action) -> this.header.setCursor(Cursor.HAND));
        this.header.setOnMouseExited((action) -> this.header.setCursor(Cursor.DEFAULT));
        this.container.setSpacing((double)10.0F);
        this.container.setAlignment(Pos.CENTER);
        this.container.setPadding(new Insets((double)10.0F));
        this.container.prefWidthProperty().bind(this.widthProperty().subtract(5));
        this.container.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.LIGHTGRAY.brighter(), new CornerRadii((double)0.0F, (double)0.0F, (double)5.0F, (double)0.0F, false), Insets.EMPTY)}));
        this.pane.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.ORANGERED.brighter(), new CornerRadii((double)0.0F, (double)0.0F, (double)0.0F, (double)5.0F, false), Insets.EMPTY)}));
        this.pane.setPrefWidth((double)5.0F);
        this.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.TRANSPARENT, new CornerRadii((double)10.0F), Insets.EMPTY)}));
        this.setEffect(new DropShadow((double)10.0F, Color.GRAY));
        this.setTop(this.header);
        this.setCenter(this.shifter);
    }

    public void addNewNotification(BorderPane borderPane, String familyName) {
        this.container.getChildren().add(0, borderPane);
        this.allNotifications.put(familyName, borderPane);
    }

    public void removeNotification(String family) {
        BorderPane pane = (BorderPane)this.allNotifications.get(family);
        if (pane != null) {
            this.container.getChildren().remove(pane);
            this.allNotifications.remove(family);
        }

    }
}
