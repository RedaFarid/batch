package com.batch.GUI.Alarms;


import com.batch.ApplicationContext;
import com.batch.Database.Entities.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.table.TableFilter;

public class AllAlarmsWindow extends Tab {

    private static volatile AllAlarmsWindow Singleton = null;

    private Stage mainWindow = null;

    private GridPane dataentery = new GridPane();
    private VBox root = new VBox();
    private VBox vbox = new VBox();
    private ToolBar toolbar = new ToolBar();

    private ObservableList<Log> list = FXCollections.observableArrayList();
    private TableView<Log> table = new TableView<>();
    private TableColumn<Log, String> identifierColumn = new TableColumn<>("Identefier");
    private TableColumn<Log, String> SourceColumn = new TableColumn<>("Source");
    private TableColumn<Log, String> EventColumn = new TableColumn<>("Event");
    private TableColumn<Log, String> UsernameColumn = new TableColumn<>("Username");
    private TableColumn<Log, String> GroupColumn = new TableColumn<>("Group");
    private TableColumn<Log, String> TimeColumn = new TableColumn<>("Time");
    private TableColumn<Log, String> DateColumn = new TableColumn<>("Date");

    private TableFilter<Log> tableFilter;

    private final AlarmsController controller;
    private final AlarmsModel model;

    private AllAlarmsWindow(Stage Window) {
        mainWindow = Window;
        this.controller = ApplicationContext.applicationContext.getBean(AlarmsController.class);
        this.model = controller.getModel();

        setStyle("-fx-background-color: SALMON;-fx-border-color: darkblue; -fx-border-width:0.1;-fx-text-fill:white;");

        graphicsBuilder();
        actionHandling();
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

        dataentery.setPadding(new Insets(10));
        dataentery.setVgap(5);
        dataentery.setHgap(5);

        //table configuration
        identifierColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        SourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        EventColumn.setCellValueFactory(new PropertyValueFactory<>("Event"));
        UsernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        GroupColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        TimeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));

        identifierColumn.prefWidthProperty().bind(table.widthProperty().divide(11));
        SourceColumn.prefWidthProperty().bind(table.widthProperty().divide(10));
        EventColumn.prefWidthProperty().bind(table.widthProperty().divide(10).multiply(4).subtract(5));
        UsernameColumn.prefWidthProperty().bind(table.widthProperty().divide(10));
        GroupColumn.prefWidthProperty().bind(table.widthProperty().divide(10));
        TimeColumn.prefWidthProperty().bind(table.widthProperty().divide(11));
        DateColumn.prefWidthProperty().bind(table.widthProperty().divide(11));

        table.getColumns().addAll(identifierColumn, SourceColumn, EventColumn, UsernameColumn, GroupColumn, TimeColumn, DateColumn);
        table.prefHeightProperty().bind(root.heightProperty().subtract(vbox.heightProperty()));
        table.setItems(model.getAllAlarmsList());
        tableFilter = TableFilter.forTableView(table).apply();
        table.setRowFactory((TableView<Log> param) -> new TableRow<Log>() {
            String style;

            @Override
            protected void updateItem(Log item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || item.getIdentifier() == null) {
                    style = ("");
                } else if (item.getIdentifier().equals("System")) {
                    style = ("-fx-background-color: blue; -fx-dark-text-color: white;-fx-mid-text-color: white;-fx-light-text-color: white;-fx-font-weight:bold;");
                } else if (item.getIdentifier().equals("Error")) {
                    style = ("-fx-background-color: Red; -fx-dark-text-color: white;-fx-mid-text-color: white;-fx-light-text-color: white;-fx-font-weight:bold;");
                } else if (item.getIdentifier().equals("Warning")) {
                    style = ("-fx-background-color: yellow;-fx-font-weight:bold;");
                } else if (item.getIdentifier().equals("Info")) {
                    style = ("-fx-font-weight:bold;");
                } else {
                    style = ("");
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                if (selected) {
                    setStyle("-fx-background-color: violet; -fx-dark-text-color: black;-fx-mid-text-color: black;-fx-light-text-color: black;-fx-font-weight:bold;");
                } else {
                    setStyle(style);
                }

            }
        });
        

        vbox.getChildren().addAll(dataentery, toolbar);
        root.getChildren().add(vbox);
        root.getChildren().add(table);

        setContent(root);
        setText("Journal alarms");
    }
    private void actionHandling() {
        model.getIsShown().bind(this.selectedProperty());
    }
}
