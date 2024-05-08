package com.batch.GUI.MaterialsWindow;


import com.batch.ApplicationContext;
import com.batch.Database.Entities.Material;
import com.batch.GUI.Controls.DataEntryPartition;
import com.batch.Utilities.RestrictiveTextField;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    private Stage mainWindow = null;


    private TableView<Material> table = new TableView<>();
    private TableColumn<Material, String> NameColumn = new TableColumn<>("ID");
    private TableColumn<Material, String> LicenceNumberColumn = new TableColumn<>("Name");
    private TableColumn<Material, String> LicenceExpirationDateColumn = new TableColumn<>("Comment");

    private Button Insert = new Button("Create new material");
    private Button Delete = new Button("Delete selected material");
    private Button Update = new Button("Update selected material");
    private Label idLabel = new Label("ID");
    private Label nameLabel = new Label("Name");
    private Label commentLabel = new Label("Comment");

    private RestrictiveTextField idField = new RestrictiveTextField();
    private RestrictiveTextField nameField = new RestrictiveTextField();
    private RestrictiveTextField commentField = new RestrictiveTextField();

    private final MaterialsController controller;
    private final MaterialsModel model;

    private MaterialsWindow(Stage Window) {
        mainWindow = Window;

        controller = ApplicationContext.applicationContext.getBean(MaterialsController.class);
        model = controller.getModel();

        graphicsBuilder();
        actionHandling();
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

        //control buttons configuration
        Insert.setPrefWidth(150);
        Delete.setPrefWidth(150);
        Update.setPrefWidth(150);

        //dataentery region configuration
        idLabel.setPrefWidth(150);
        nameLabel.setPrefWidth(150);
        commentLabel.setPrefWidth(150);

        idField.setPrefWidth(250);
        nameField.setPrefWidth(250);
        commentField.setPrefWidth(250);

        idField.setDisable(true);

        dataEntryPartition.add(idLabel, 1, 2);
        dataEntryPartition.add(nameLabel, 3, 2);
        dataEntryPartition.add(commentLabel, 1, 3);

        dataEntryPartition.add(idField, 2, 2);
        dataEntryPartition.add(nameField, 4, 2);
        dataEntryPartition.add(commentField, 2, 3);

        //table configuration
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        LicenceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        LicenceExpirationDateColumn.setCellValueFactory(new PropertyValueFactory<>("Comment"));

        table.getColumns().addAll(NameColumn, LicenceNumberColumn, LicenceExpirationDateColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.prefHeightProperty().bind(root.heightProperty().subtract(vbox.heightProperty()));
        table.setItems(model.getList());

        //stage configuration

        hbox.getItems().addAll(Insert, Update, Delete);
        hbox.setPadding(new Insets(10, 10, 10, 10));

        vbox.getChildren().addAll(dataEntryPartition, hbox);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);

        root.getChildren().add(vbox);
        root.getChildren().add(table);
        root.setPadding(new Insets(10));

        initOwner(mainWindow);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        setScene(new Scene(root, 700, 800));
        setTitle("Materials manager");
    }

    private void actionHandling() {
        model.getIsShown().bind(showingProperty());
        Insert.setOnMouseClicked(action -> {
            if ((nameField.getText().length() > 0) && (commentField.getText().length() > 0)) {
                Material material = new Material(nameField.getText(), commentField.getText());
                controller.save(material);

            } else {
                Alert Error = new Alert(Alert.AlertType.ERROR);
                Error.setTitle("Error ");
                Error.setHeaderText("Error Inserting data");
                Error.setContentText("Material already exist , please check entered data ...");
                Error.initOwner(this);
                Error.showAndWait();
            }
        });

        Delete.setOnMouseClicked(action -> {
            controller.delete(idField.getText());

        });

        Update.setOnMouseClicked(action -> {
            if ((idField.getText().length() > 0) && (nameField.getText().length() > 0)) {
                Material material = new Material(nameField.getText(), commentField.getText());

                controller.save(material);

            } else {
                Alert Error = new Alert(Alert.AlertType.ERROR);
                Error.setTitle("Error ");
                Error.setHeaderText("Error Inserting data");
                Error.setContentText("Material not exist , please check entered data ...");
                Error.initOwner(this);
                Error.showAndWait();
            }
        });

        table.setOnMousePressed(action -> {
            if (action.getClickCount() == 1 && table.getItems().size() > 0 && !table.getSelectionModel().isEmpty()) {
                try {
                    Material selected = table.getSelectionModel().getSelectedItem();
                    idField.setText(String.valueOf(selected.getId()));
                    nameField.setText(selected.getName());
                    commentField.setText(selected.getComment());
                } catch (Exception e) {
                    Alert Error = new Alert(Alert.AlertType.ERROR);
                    Error.setTitle("Error ");
                    Error.setHeaderText("Error Importing data");
                    Error.setContentText("Please select Table row again");
                    Error.initOwner(this);
                    Error.showAndWait();
                }
            }
        });
    }


    @Override
    public void close() {
        hide();
    }
}
