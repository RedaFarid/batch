package com.batch.GUI.Alarms;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;

public class AllAlarmsWindow extends Tab {
    private static volatile AllAlarmsWindow Singleton = null;
    private final AlarmsController controller;
    private final AlarmsModel model;
    private Stage mainWindow = null;
    private final GridPane dataentery = new GridPane();
    private final VBox root = new VBox();
    private final VBox vbox = new VBox();
    private final ToolBar toolbar = new ToolBar();
    private final ObservableList<Log> list = FXCollections.observableArrayList();
    private final TableView<Log> table = new TableView();
    private final TableColumn<Log, String> identifierColumn = new TableColumn("Identefier");
    private final TableColumn<Log, String> SourceColumn = new TableColumn("Source");
    private final TableColumn<Log, String> EventColumn = new TableColumn("Event");
    private final TableColumn<Log, String> UsernameColumn = new TableColumn("Username");
    private final TableColumn<Log, String> GroupColumn = new TableColumn("Group");
    private final TableColumn<Log, String> TimeColumn = new TableColumn("Time");
    private final TableColumn<Log, String> DateColumn = new TableColumn("Date");
    private TableFilter<Log> tableFilter;

    private AllAlarmsWindow(Stage Window) {
        this.mainWindow = Window;
        this.controller = ApplicationContext.applicationContext.getBean(AlarmsController.class);
        this.model = this.controller.getModel();
        this.setStyle("-fx-background-color: SALMON;-fx-border-color: darkblue; -fx-border-width:0.1;-fx-text-fill:white;");
        this.graphicsBuilder();
        this.actionHandling();
    }

    public static AllAlarmsWindow getWindow(Stage Window) {
        synchronized (AllAlarmsWindow.class) {
            if (Singleton == null) {
                Singleton = new AllAlarmsWindow(Window);
            }
        }

        return Singleton;
    }

    private void graphicsBuilder() {
        this.dataentery.setPadding(new Insets(10.0F));
        this.dataentery.setVgap(5.0F);
        this.dataentery.setHgap(5.0F);
        this.identifierColumn.setCellValueFactory(new PropertyValueFactory("identifier"));
        this.SourceColumn.setCellValueFactory(new PropertyValueFactory("source"));
        this.EventColumn.setCellValueFactory(new PropertyValueFactory("Event"));
        this.UsernameColumn.setCellValueFactory(new PropertyValueFactory("userName"));
        this.GroupColumn.setCellValueFactory(new PropertyValueFactory("groupName"));
        this.TimeColumn.setCellValueFactory(new PropertyValueFactory("time"));
        this.DateColumn.setCellValueFactory(new PropertyValueFactory("Date"));
        this.identifierColumn.prefWidthProperty().bind(this.table.widthProperty().divide(11));
        this.SourceColumn.prefWidthProperty().bind(this.table.widthProperty().divide(10));
        this.EventColumn.prefWidthProperty().bind(this.table.widthProperty().divide(10).multiply(4).subtract(5));
        this.UsernameColumn.prefWidthProperty().bind(this.table.widthProperty().divide(10));
        this.GroupColumn.prefWidthProperty().bind(this.table.widthProperty().divide(10));
        this.TimeColumn.prefWidthProperty().bind(this.table.widthProperty().divide(11));
        this.DateColumn.prefWidthProperty().bind(this.table.widthProperty().divide(11));
        this.table.getColumns().addAll(this.identifierColumn, this.SourceColumn, this.EventColumn, this.UsernameColumn, this.GroupColumn, this.TimeColumn, this.DateColumn);
        this.table.prefHeightProperty().bind(this.root.heightProperty().subtract(this.vbox.heightProperty()));
        this.table.setItems(this.model.getAllAlarmsList());
        this.tableFilter = TableFilter.forTableView(this.table).apply();
        this.table.setRowFactory((param) -> new TableRow<Log>() {
            String style;

            protected void updateItem(Log item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getIdentifier() != null) {
                    if (item.getIdentifier().equals("System")) {
                        this.style = "-fx-background-color: blue; -fx-dark-text-color: white;-fx-mid-text-color: white;-fx-light-text-color: white;-fx-font-weight:bold;";
                    } else if (item.getIdentifier().equals("Error")) {
                        this.style = "-fx-background-color: Red; -fx-dark-text-color: white;-fx-mid-text-color: white;-fx-light-text-color: white;-fx-font-weight:bold;";
                    } else if (item.getIdentifier().equals("Warning")) {
                        this.style = "-fx-background-color: yellow;-fx-font-weight:bold;";
                    } else if (item.getIdentifier().equals("Info")) {
                        this.style = "-fx-font-weight:bold;";
                    } else {
                        this.style = "";
                    }
                } else {
                    this.style = "";
                }

            }

            public void updateSelected(boolean selected) {
                if (selected) {
                    this.setStyle("-fx-background-color: violet; -fx-dark-text-color: black;-fx-mid-text-color: black;-fx-light-text-color: black;-fx-font-weight:bold;");
                } else {
                    this.setStyle(this.style);
                }

            }
        });
        this.vbox.getChildren().addAll(this.dataentery, this.toolbar);
        this.root.getChildren().add(this.vbox);
        this.root.getChildren().add(this.table);
        this.setContent(this.root);
        this.setText("Journal alarms");
    }

    private void actionHandling() {
        this.model.getIsShown().bind(this.selectedProperty());
    }
}
