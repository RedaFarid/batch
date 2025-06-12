
package com.batch.GUI.UnitsWindow;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Unit;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UnitsWindow extends Stage {
    private static UnitsWindow singelton = null;
    private Stage mainWindow;
    private ToolBar bar = new ToolBar();
    private Label label = new Label("Unit name");
    private TextField field = new TextField();
    private Button button = new Button("Add new Unit");
    private BorderPane root = new BorderPane();
    private ListView<Unit> view = new ListView();
    private final UnitsController controller;
    private final UnitsModel model;

    private UnitsWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = (UnitsController)ApplicationContext.applicationContext.getBean(UnitsController.class);
        this.model = this.controller.getModel();
        this.graphicsBuilder();
        this.actionHandler();
    }

    public static UnitsWindow getWindow(Stage stage) {
        synchronized(UnitsWindow.class) {
            if (singelton == null) {
                singelton = new UnitsWindow(stage);
            }
        }

        return singelton;
    }

    private void graphicsBuilder() {
        this.initOwner(this.mainWindow);
        this.setScene(new Scene(this.root));
        this.view.setItems(this.model.getList());
        this.bar.getItems().addAll(new Node[]{this.label, this.field, this.button});
        this.root.setTop(this.bar);
        this.root.setCenter(this.view);
    }

    private void actionHandler() {
        this.model.getIsShown().bind(this.showingProperty());
        this.button.setOnMouseClicked((actio) -> {
            try {
                if (!this.field.getText().isEmpty()) {
                    Unit unit = new Unit(this.field.getText());
                    if (this.controller.isUnitExist(unit)) {
                        this.controller.saveUnit(unit);
                    } else {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setContentText("Dublicated units or Error inserting in database");
                        alert.initOwner(this);
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setContentText("Empty name entered");
                    alert.initOwner(this);
                    alert.show();
                }
            } catch (Exception var4) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Error inserting in database");
                alert.initOwner(this);
                alert.show();
            }

        });
    }

    public String toString() {
        return "Units window";
    }
}
