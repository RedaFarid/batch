package com.batch.GUI.PhasesWindow;

import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseInformationDTO;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Entities.Phase;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PhasesWindow extends Stage {
    private static PhasesWindow singelton = null;
    private final PhasesController controller;
    private final PhasesModel model;
    private final Stage mainWindow;
    private final BorderPane root = new BorderPane();
    private final GridPane controlContainer = new GridPane();
    private final Label idLabel = new Label("ID number :");
    private final TextField idField = new TextField();
    private final Label phaseLabel = new Label("Phase name");
    private final TextField phaseField = new TextField();
    private final Label unitLabel = new Label("Unit name :");
    private final ComboBox<String> units = new ComboBox();
    private final Label parameterNameLabel = new Label("Parameter name ");
    private final TextField ParameterNameField = new TextField();
    private final Label parameterTypeLabel = new Label("Parameter Type ");
    private final ComboBox<String> parameterTypeField = new ComboBox();
    private final Label phaseTypeLabel = new Label("Phase Type ");
    private final ComboBox<String> phaseTypeField = new ComboBox();
    private final Button add = new Button("Add new phase");
    private final Button clear = new Button("Delete all phases");
    private final TreeTableView<PhaseInformationDTO> table = new TreeTableView();
    private final TreeItem rootItem = new TreeItem("Phases");
    private final TreeTableColumn<PhaseInformationDTO, String> id = new TreeTableColumn("ID");
    private final TreeTableColumn<PhaseInformationDTO, Double> name = new TreeTableColumn("Name");
    private final TreeTableColumn<PhaseInformationDTO, Double> unit = new TreeTableColumn("Unit");
    private final TreeTableColumn<PhaseInformationDTO, Double> phaseType = new TreeTableColumn("Phase Type");
    private final TreeTableColumn<PhaseInformationDTO, Double> parameterName = new TreeTableColumn("Parameter");
    private final TreeTableColumn<PhaseInformationDTO, Double> parameterType = new TreeTableColumn("Parameter Type");

    private PhasesWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = ApplicationContext.applicationContext.getBean(PhasesController.class);
        this.model = this.controller.getModel();
        this.model.setRootItem(this.rootItem);
        this.graphicsBuilder();
        this.actionHandler();
    }

    public static PhasesWindow getWindow(Stage stage) {
        synchronized (PhasesWindow.class) {
            if (singelton == null) {
                singelton = new PhasesWindow(stage);
            }
        }

        return singelton;
    }

    private void graphicsBuilder() {
        this.initOwner(this.mainWindow);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setScene(new Scene(this.root));
        this.setHeight(800.0F);
        this.setResizable(false);
        this.setTitle("Phases manager");
        this.id.setCellValueFactory(new TreeItemPropertyValueFactory("id"));
        this.name.setCellValueFactory(new TreeItemPropertyValueFactory("name"));
        this.unit.setCellValueFactory(new TreeItemPropertyValueFactory("unit"));
        this.phaseType.setCellValueFactory(new TreeItemPropertyValueFactory("phaseType"));
        this.parameterName.setCellValueFactory(new TreeItemPropertyValueFactory("ParameterName"));
        this.parameterType.setCellValueFactory(new TreeItemPropertyValueFactory("ParameterType"));
        this.table.getColumns().addAll(this.id, this.name, this.unit, this.phaseType, this.parameterName, this.parameterType);
        this.table.setRoot(this.rootItem);
        this.table.setShowRoot(false);
        this.id.prefWidthProperty().bind(this.table.widthProperty().divide(13));
        this.name.prefWidthProperty().bind(this.table.widthProperty().divide(13).multiply(4));
        this.unit.prefWidthProperty().bind(this.table.widthProperty().divide(13).multiply(2));
        this.phaseType.prefWidthProperty().bind(this.table.widthProperty().divide(13).multiply(2));
        this.parameterName.prefWidthProperty().bind(this.table.widthProperty().divide(13).multiply(2));
        this.parameterType.prefWidthProperty().bind(this.table.widthProperty().divide(13).multiply(2));
        this.idLabel.setPrefWidth(150.0F);
        this.phaseLabel.setPrefWidth(150.0F);
        this.unitLabel.setPrefWidth(150.0F);
        this.parameterNameLabel.setPrefWidth(150.0F);
        this.parameterTypeLabel.setPrefWidth(150.0F);
        this.phaseTypeLabel.setPrefWidth(150.0F);
        this.idField.setPrefWidth(250.0F);
        this.phaseField.setPrefWidth(670.0F);
        this.units.setPrefWidth(670.0F);
        this.ParameterNameField.setPrefWidth(250.0F);
        this.parameterTypeField.setPrefWidth(250.0F);
        this.phaseTypeField.setPrefWidth(670.0F);
        this.add.setPrefWidth(150.0F);
        this.clear.setPrefWidth(150.0F);
        this.parameterTypeField.setItems(FXCollections.observableArrayList((Collection) Arrays.stream(PhaseParameterType.values()).map(Enum::name).map((item) -> item.replace("_", " ")).collect(Collectors.toList())));
        this.phaseTypeField.setItems(FXCollections.observableArrayList((Collection) Arrays.stream(PhasesTypes.values()).map(Enum::name).map((item) -> item.replace("_", " ")).collect(Collectors.toList())));
        this.controlContainer.setPadding(new Insets(10.0F));
        this.controlContainer.setHgap(5.0F);
        this.controlContainer.setVgap(5.0F);
        this.controlContainer.add(this.idLabel, 1, 0);
        this.controlContainer.add(this.idField, 2, 0);
        this.controlContainer.add(this.phaseLabel, 1, 1);
        this.controlContainer.add(this.phaseField, 2, 1, 5, 1);
        this.controlContainer.add(this.parameterNameLabel, 1, 4);
        this.controlContainer.add(this.ParameterNameField, 2, 4);
        this.controlContainer.add(this.parameterTypeLabel, 5, 4);
        this.controlContainer.add(this.parameterTypeField, 6, 4);
        this.controlContainer.add(this.unitLabel, 1, 2, 5, 1);
        this.controlContainer.add(this.units, 2, 2, 5, 1);
        this.controlContainer.add(this.phaseTypeLabel, 1, 3, 5, 1);
        this.controlContainer.add(this.phaseTypeField, 2, 3, 5, 1);
        this.controlContainer.add(this.add, 1, 5);
        this.controlContainer.add(this.clear, 2, 5);
        this.root.setPadding(new Insets(10.0F));
        this.root.setTop(this.controlContainer);
        this.root.setCenter(this.table);
    }

    private void actionHandler() {
        this.controller.refresh();
        this.onShowingProperty().addListener((observable, oldValue, newValue) -> this.controller.refresh());
        this.units.setOnMouseClicked((action) -> this.units.setItems(this.controller.getUnitsName()));
        this.phaseTypeField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(PhasesTypes.Dose_phase.name().replace("_", " "))) {
                this.ParameterNameField.setText("Percentage %");
                this.parameterTypeField.setValue(PhaseParameterType.Value.name());
                this.ParameterNameField.setEditable(false);
                this.parameterTypeField.setDisable(true);
            } else {
                this.ParameterNameField.setText("");
                this.ParameterNameField.setEditable(true);
                this.parameterTypeField.setDisable(false);
            }

        });
        this.add.setOnMouseClicked((action) -> {
            try {
                if (this.units.getValue() != null && this.idField.getText() != null && this.phaseField.getText() != null && !this.idField.getText().isEmpty() && !this.phaseField.getText().isEmpty()) {
                    this.controller.findPhaseById(this.idField.getText()).ifPresentOrElse((checkPhase) -> {
                        List<Parameter> parametersData = new LinkedList();
                        parametersData.add(new Parameter(this.ParameterNameField.getText(), this.parameterTypeField.getValue()));
                        Phase phase = new Phase(Long.parseLong(this.idField.getText()), this.phaseField.getText(), this.units.getValue(), this.phaseTypeField.getValue(), parametersData);

                        try {
                            this.controller.createNewPhase(phase);
                        } catch (Exception var6) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setContentText("Error inserting in database or id format is not number");
                            alert.initOwner(this);
                            alert.show();
                        }

                        this.controller.refresh();
                    }, () -> {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setContentText("Selected ID already inserted in the database.");
                        alert.initOwner(this);
                        alert.show();
                    });
                } else {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setContentText("Empty data . \nentered");
                    alert.initOwner(this);
                    alert.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Error inserting in database or id format is not number");
                alert.initOwner(this);
                alert.show();
            }

        });
        this.clear.setOnMouseClicked((action) -> this.controller.clearAllPhases());
        this.table.setOnContextMenuRequested((action) -> {
            if (this.table.getSelectionModel().getSelectedItem() != null) {
                ContextMenu menu = new ContextMenu();
                MenuItem deletePhase = new MenuItem("Delete phase      ");
                MenuItem addParameter = new MenuItem("Add parameter    ");
                MenuItem deleteParameter = new MenuItem("Delete parameter    ");
                if (this.table.getTreeItemLevel(this.table.getSelectionModel().getSelectedItem()) == 1) {
                    menu.getItems().addAll(addParameter, deletePhase);
                } else if (this.table.getTreeItemLevel(this.table.getSelectionModel().getSelectedItem()) == 2) {
                    menu.getItems().addAll(deleteParameter);
                }

                menu.show(this, action.getScreenX(), action.getScreenY());
                addParameter.setOnAction((event) -> this.newParameterWindow(((PhaseInformationDTO) ((TreeItem) this.table.getSelectionModel().getSelectedItem()).getValue()).getId()));
                deleteParameter.setOnAction((event) -> this.controller.deleteParameterFromPhase(((PhaseInformationDTO) ((TreeItem) this.table.getSelectionModel().getSelectedItem()).getParent().getValue()).getId(), ((PhaseInformationDTO) ((TreeItem) this.table.getSelectionModel().getSelectedItem()).getValue()).getParameterName()));
                deletePhase.setOnAction((event) -> this.controller.deletePhase(((PhaseInformationDTO) ((TreeItem) this.table.getSelectionModel().getSelectedItem()).getValue()).getId()));
            }

        });
    }

    private void newParameterWindow(String id) {
        GridPane rootPane = new GridPane();
        Button addNewPara = new Button("Add new phase");
        Label parameterLabel = new Label("Parameter name ");
        TextField parameter = new TextField();
        Label typeLabel = new Label("Parameter Type ");
        ComboBox<String> type = new ComboBox();
        parameterLabel.setPrefWidth(150.0F);
        typeLabel.setPrefWidth(150.0F);
        parameter.setPrefWidth(250.0F);
        type.setPrefWidth(250.0F);
        addNewPara.setPrefWidth(150.0F);
        rootPane.add(parameterLabel, 1, 1);
        rootPane.add(parameter, 2, 1);
        rootPane.add(typeLabel, 1, 2);
        rootPane.add(type, 2, 2);
        rootPane.add(addNewPara, 1, 5);
        rootPane.setPadding(new Insets(10.0F));
        rootPane.setVgap(4.0F);
        rootPane.setHgap(10.0F);
        type.setItems(FXCollections.observableArrayList((Collection) Arrays.stream(PhaseParameterType.values()).map(Enum::name).map((item) -> item.replace("_", " ")).collect(Collectors.toList())));
        Scene scene = new Scene(rootPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(this);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        addNewPara.setOnMouseClicked((action) -> {
            if (!parameter.getText().isEmpty() && type.getValue() != null) {
                this.controller.addParameterToPhase(id, parameter.getText(), type.getValue());
            }

            stage.hide();
        });
        stage.showAndWait();
    }
}
