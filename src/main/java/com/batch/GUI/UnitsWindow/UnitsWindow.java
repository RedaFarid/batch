package com.batch.GUI.UnitsWindow;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Unit;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private ListView<Unit> view = new ListView<>();

    private final UnitsController controller;
    private final UnitsModel model;

    private UnitsWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = ApplicationContext.applicationContext.getBean(UnitsController.class);
        this.model = controller.getModel();

        graphicsBuilder();
        actionHandler();
    }

    public static UnitsWindow getWindow(Stage stage) {
        synchronized (UnitsWindow.class) {
            if (singelton == null) {
                singelton = new UnitsWindow(stage);
            }
        }
        return singelton;
    }

    private void graphicsBuilder() {
        initOwner(mainWindow);
        setScene(new Scene(root));

        view.setItems(model.getList());

        bar.getItems().addAll(label, field, button);
        root.setTop(bar);
        root.setCenter(view);
    }

    private void actionHandler() {
        model.getIsShown().bind(showingProperty());
        button.setOnMouseClicked(actio -> {
            try {
                if (!field.getText().isEmpty()) {
                    Unit unit = new Unit(field.getText());
                    if (controller.isUnitExist(unit)) {
                        controller.saveUnit(unit);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Dublicated units or Error inserting in database");
                        alert.initOwner(this);
                        alert.show();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Empty name entered");
                    alert.initOwner(this);
                    alert.show();
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error inserting in database");
                alert.initOwner(this);
                alert.show();
            }
        });
    }

    @Override
    public String toString() {
        return "Units window";
    }

}
