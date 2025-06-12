package com.batch.GUI.MaterialsWindow;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Material;
import com.batch.GUI.Controls.DataEntryPartition;
import com.batch.Utilities.RestrictiveTextField;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MaterialsWindow extends Stage {
    private static volatile MaterialsWindow Singleton = null;
    private final DataEntryPartition dataEntryPartition = new DataEntryPartition("Material");
    private final VBox root = new VBox();
    private final VBox vbox = new VBox();
    private final ToolBar hbox = new ToolBar();
    private final MaterialsController controller;
    private final MaterialsModel model;
    private Stage mainWindow = null;
    private final TableView<Material> table = new TableView();
    private final TableColumn<Material, String> NameColumn = new TableColumn("ID");
    private final TableColumn<Material, String> LicenceNumberColumn = new TableColumn("Name");
    private final TableColumn<Material, String> LicenceExpirationDateColumn = new TableColumn("Comment");
    private final Button Insert = new Button("Create new material");
    private final Button Delete = new Button("Delete selected material");
    private final Button Update = new Button("Update selected material");
    private final Label idLabel = new Label("ID");
    private final Label nameLabel = new Label("Name");
    private final Label commentLabel = new Label("Comment");
    private final RestrictiveTextField idField = new RestrictiveTextField();
    private final RestrictiveTextField nameField = new RestrictiveTextField();
    private final RestrictiveTextField commentField = new RestrictiveTextField();

    private MaterialsWindow(Stage Window) {
        this.mainWindow = Window;
        this.controller = ApplicationContext.applicationContext.getBean(MaterialsController.class);
        this.model = this.controller.getModel();
        this.graphicsBuilder();
        this.actionHandling();
    }

    public static MaterialsWindow getMaterialsWindow(Stage Window) {
        synchronized (MaterialsWindow.class) {
            if (Singleton == null) {
                Singleton = new MaterialsWindow(Window);
            }
        }

        return Singleton;
    }

    private void graphicsBuilder() {
        this.Insert.setPrefWidth(150.0F);
        this.Delete.setPrefWidth(150.0F);
        this.Update.setPrefWidth(150.0F);
        this.idLabel.setPrefWidth(150.0F);
        this.nameLabel.setPrefWidth(150.0F);
        this.commentLabel.setPrefWidth(150.0F);
        this.idField.setPrefWidth(250.0F);
        this.nameField.setPrefWidth(250.0F);
        this.commentField.setPrefWidth(250.0F);
        this.idField.setDisable(true);
        this.dataEntryPartition.add(this.idLabel, 1, 2);
        this.dataEntryPartition.add(this.nameLabel, 3, 2);
        this.dataEntryPartition.add(this.commentLabel, 1, 3);
        this.dataEntryPartition.add(this.idField, 2, 2);
        this.dataEntryPartition.add(this.nameField, 4, 2);
        this.dataEntryPartition.add(this.commentField, 2, 3);
        this.NameColumn.setCellValueFactory(new PropertyValueFactory("id"));
        this.LicenceNumberColumn.setCellValueFactory(new PropertyValueFactory("name"));
        this.LicenceExpirationDateColumn.setCellValueFactory(new PropertyValueFactory("Comment"));
        this.table.getColumns().addAll(this.NameColumn, this.LicenceNumberColumn, this.LicenceExpirationDateColumn);
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.table.prefHeightProperty().bind(this.root.heightProperty().subtract(this.vbox.heightProperty()));
        this.table.setItems(this.model.getList());
        this.hbox.getItems().addAll(this.Insert, this.Update, this.Delete);
        this.hbox.setPadding(new Insets(10.0F, 10.0F, 10.0F, 10.0F));
        this.vbox.getChildren().addAll(this.dataEntryPartition, this.hbox);
        this.vbox.setPadding(new Insets(10.0F, 10.0F, 10.0F, 10.0F));
        this.vbox.setSpacing(10.0F);
        this.root.getChildren().add(this.vbox);
        this.root.getChildren().add(this.table);
        this.root.setPadding(new Insets(10.0F));
        this.initOwner(this.mainWindow);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setScene(new Scene(this.root, 700.0F, 800.0F));
        this.setTitle("Materials manager");
    }

    private void actionHandling() {
        this.model.getIsShown().bind(this.showingProperty());
        this.Insert.setOnMouseClicked((action) -> {
            if (this.nameField.getText().length() > 0 && this.commentField.getText().length() > 0) {
                Material material = new Material(this.nameField.getText(), this.commentField.getText());
                this.controller.save(material);
            } else {
                Alert Error = new Alert(AlertType.ERROR);
                Error.setTitle("Error ");
                Error.setHeaderText("Error Inserting data");
                Error.setContentText("Material already exist , please check entered data ...");
                Error.initOwner(this);
                Error.showAndWait();
            }

        });
        this.Delete.setOnMouseClicked((action) -> this.controller.delete(this.idField.getText()));
        this.Update.setOnMouseClicked((action) -> {
            if (this.idField.getText().length() > 0 && this.nameField.getText().length() > 0) {
                Material material = new Material(this.nameField.getText(), this.commentField.getText());
                this.controller.save(material);
            } else {
                Alert Error = new Alert(AlertType.ERROR);
                Error.setTitle("Error ");
                Error.setHeaderText("Error Inserting data");
                Error.setContentText("Material not exist , please check entered data ...");
                Error.initOwner(this);
                Error.showAndWait();
            }

        });
        this.table.setOnMousePressed((action) -> {
            if (action.getClickCount() == 1 && this.table.getItems().size() > 0 && !this.table.getSelectionModel().isEmpty()) {
                try {
                    Material selected = this.table.getSelectionModel().getSelectedItem();
                    this.idField.setText(String.valueOf(selected.getId()));
                    this.nameField.setText(selected.getName());
                    this.commentField.setText(selected.getComment());
                } catch (Exception var4) {
                    Alert Error = new Alert(AlertType.ERROR);
                    Error.setTitle("Error ");
                    Error.setHeaderText("Error Importing data");
                    Error.setContentText("Please select Table row again");
                    Error.initOwner(this);
                    Error.showAndWait();
                }
            }

        });
    }

    public void close() {
        this.hide();
    }
}
