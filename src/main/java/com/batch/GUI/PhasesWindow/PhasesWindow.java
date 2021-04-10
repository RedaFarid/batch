package com.batch.GUI.PhasesWindow;


import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseInformationDTO;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Entities.Phase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PhasesWindow extends Stage {

    private static PhasesWindow singelton = null;
    private Stage mainWindow;

    private BorderPane root = new BorderPane();
    private GridPane controlContainer = new GridPane();

    private Label idLabel = new Label("ID number :");
    private TextField idField = new TextField();

    private Label phaseLabel = new Label("Phase name");
    private TextField phaseField = new TextField();

    private Label unitLabel = new Label("Unit name :");
    private ComboBox<String> units = new ComboBox<>();

    private Label parameterNameLabel = new Label("Parameter name ");
    private TextField ParameterNameField = new TextField();

    private Label parameterTypeLabel = new Label("Parameter Type ");
    private ComboBox<String> parameterTypeField = new ComboBox();

    private Label phaseTypeLabel = new Label("Phase Type ");
    private ComboBox<String> phaseTypeField = new ComboBox();

    private Button add = new Button("Add new phase");
    private Button clear = new Button("Delete all phases");

    private TreeTableView<PhaseInformationDTO> table = new TreeTableView<>();
    private TreeItem rootItem = new TreeItem("Phases");
    private TreeTableColumn<PhaseInformationDTO, String> id = new TreeTableColumn<>("ID");
    private TreeTableColumn<PhaseInformationDTO, Double> name = new TreeTableColumn<>("Name");
    private TreeTableColumn<PhaseInformationDTO, Double> unit = new TreeTableColumn<>("Unit");
    private TreeTableColumn<PhaseInformationDTO, Double> phaseType = new TreeTableColumn<>("Phase Type");
    private TreeTableColumn<PhaseInformationDTO, Double> parameterName = new TreeTableColumn<>("Parameter");
    private TreeTableColumn<PhaseInformationDTO, Double> parameterType = new TreeTableColumn<>("Parameter Type");

    private final PhasesController controller;
    private final PhasesModel model;

    private PhasesWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = ApplicationContext.applicationContext.getBean(PhasesController.class);
        this.model = controller.getModel();
        model.setRootItem(rootItem);
        graphicsBuilder();
        actionHandler();
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
        initOwner(mainWindow);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        setScene(new Scene(root));
        setHeight(800);
        setResizable(false);
        setTitle("Phases manager");

        id.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        name.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        unit.setCellValueFactory(new TreeItemPropertyValueFactory<>("unit"));
        phaseType.setCellValueFactory(new TreeItemPropertyValueFactory<>("phaseType"));
        parameterName.setCellValueFactory(new TreeItemPropertyValueFactory<>("ParameterName"));
        parameterType.setCellValueFactory(new TreeItemPropertyValueFactory<>("ParameterType"));

        table.getColumns().addAll(id, name, unit, phaseType, parameterName, parameterType);
        table.setRoot(rootItem);
        table.setShowRoot(false);

        id.prefWidthProperty().bind(table.widthProperty().divide(13));
        name.prefWidthProperty().bind(table.widthProperty().divide(13).multiply(4));
        unit.prefWidthProperty().bind(table.widthProperty().divide(13).multiply(2));
        phaseType.prefWidthProperty().bind(table.widthProperty().divide(13).multiply(2));
        parameterName.prefWidthProperty().bind(table.widthProperty().divide(13).multiply(2));
        parameterType.prefWidthProperty().bind(table.widthProperty().divide(13).multiply(2));

        idLabel.setPrefWidth(150);
        phaseLabel.setPrefWidth(150);
        unitLabel.setPrefWidth(150);
        parameterNameLabel.setPrefWidth(150);
        parameterTypeLabel.setPrefWidth(150);
        phaseTypeLabel.setPrefWidth(150);

        idField.setPrefWidth(250);
        phaseField.setPrefWidth(670);
        units.setPrefWidth(670);
        ParameterNameField.setPrefWidth(250);
        parameterTypeField.setPrefWidth(250);
        phaseTypeField.setPrefWidth(670);

        add.setPrefWidth(150);
        clear.setPrefWidth(150);

        parameterTypeField.setItems(FXCollections.observableArrayList(Arrays.stream(PhaseParameterType.values()).map(Enum::name).map(item -> item.replace("_", " ")).collect(Collectors.toList())));
        phaseTypeField.setItems(FXCollections.observableArrayList(Arrays.stream(PhasesTypes.values()).map(Enum::name).map(item -> item.replace("_", " ")).collect(Collectors.toList())));

        controlContainer.setPadding(new Insets(10));
        controlContainer.setHgap(5);
        controlContainer.setVgap(5);

        controlContainer.add(idLabel, 1, 0);
        controlContainer.add(idField, 2, 0);

        controlContainer.add(phaseLabel, 1, 1);
        controlContainer.add(phaseField, 2, 1, 5, 1);

        controlContainer.add(parameterNameLabel, 1, 4);
        controlContainer.add(ParameterNameField, 2, 4);

        controlContainer.add(parameterTypeLabel, 5, 4);
        controlContainer.add(parameterTypeField, 6, 4);

        controlContainer.add(unitLabel, 1, 2, 5, 1);
        controlContainer.add(units, 2, 2, 5, 1);

        controlContainer.add(phaseTypeLabel, 1, 3, 5, 1);
        controlContainer.add(phaseTypeField, 2, 3, 5, 1);

        controlContainer.add(add, 1, 5);
        controlContainer.add(clear, 2, 5);

        root.setPadding(new Insets(10));
        root.setTop(controlContainer);
        root.setCenter(table);
    }

    private void actionHandler() {
        controller.refresh();
        onShowingProperty().addListener((observable, oldValue, newValue) -> controller.refresh());
        units.setOnMouseClicked(action -> units.setItems(controller.getUnitsName()));
        phaseTypeField.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(PhasesTypes.Dose_phase.name().replace("_", " "))) {
                ParameterNameField.setText("Percentage %");
                parameterTypeField.setValue(PhaseParameterType.Value.name());

                ParameterNameField.setEditable(false);
                parameterTypeField.setDisable(true);

            } else {
                ParameterNameField.setText("");

                ParameterNameField.setEditable(true);
                parameterTypeField.setDisable(false);
            }
        });
        add.setOnMouseClicked(action -> {
            try {
                if (!(units.getValue() == null || idField.getText() == null || phaseField.getText() == null || idField.getText().isEmpty() || phaseField.getText().isEmpty())) {
                    controller.findPhaseById(idField.getText()).ifPresentOrElse(checkPhase -> {
                        List<Parameter> parametersData = new LinkedList<>();
                        parametersData.add(new Parameter(ParameterNameField.getText(), parameterTypeField.getValue()));
                        Phase phase = new Phase(Long.parseLong(idField.getText()), phaseField.getText(), units.getValue(), phaseTypeField.getValue(), parametersData);
                        try {
                            controller.createNewPhase(phase);
                        }catch (Exception e){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Error inserting in database or id format is not number");
                            alert.initOwner(this);
                            alert.show();
                        }
                        controller.refresh();
                    }, () -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Selected ID already inserted in the database.");
                        alert.initOwner(this);
                        alert.show();
                    });
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Empty data . \nentered");
                    alert.initOwner(this);
                    alert.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error inserting in database or id format is not number");
                alert.initOwner(this);
                alert.show();
            }
        });

        clear.setOnMouseClicked(action -> controller.clearAllPhases());
        table.setOnContextMenuRequested(action -> {
            if (table.getSelectionModel().getSelectedItem() != null) {
                ContextMenu menu = new ContextMenu();
                MenuItem deletePhase = new MenuItem("Delete phase      ");
                MenuItem addParameter = new MenuItem("Add parameter    ");
                MenuItem deleteParameter = new MenuItem("Delete parameter    ");
                if (table.getTreeItemLevel(table.getSelectionModel().getSelectedItem()) == 1) {
                    menu.getItems().addAll(addParameter, deletePhase);
                } else if (table.getTreeItemLevel(table.getSelectionModel().getSelectedItem()) == 2) {
                    menu.getItems().addAll(deleteParameter);
                }
                menu.show(this, action.getScreenX(), action.getScreenY());
                addParameter.setOnAction(event -> {
                    newParameterWindow(table.getSelectionModel().getSelectedItem().getValue().getId());
                });
                deleteParameter.setOnAction(event -> controller.deleteParameterFromPhase(table.getSelectionModel().getSelectedItem().getParent().getValue().getId(), table.getSelectionModel().getSelectedItem().getValue().getParameterName()));
                deletePhase.setOnAction(event -> controller.deletePhase(table.getSelectionModel().getSelectedItem().getValue().getId()));
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

        parameterLabel.setPrefWidth(150);
        typeLabel.setPrefWidth(150);

        parameter.setPrefWidth(250);
        type.setPrefWidth(250);

        addNewPara.setPrefWidth(150);

        rootPane.add(parameterLabel, 1, 1);
        rootPane.add(parameter, 2, 1);

        rootPane.add(typeLabel, 1, 2);
        rootPane.add(type, 2, 2);

        rootPane.add(addNewPara, 1, 5);

        rootPane.setPadding(new Insets(10));
        rootPane.setVgap(4);
        rootPane.setHgap(10);

        type.setItems(FXCollections.observableArrayList(Arrays.stream(PhaseParameterType.values()).map(Enum::name).map(item -> item.replace("_", " ")).collect(Collectors.toList())));

        Scene scene = new Scene(rootPane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(this);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        addNewPara.setOnMouseClicked(action -> {
            if (!parameter.getText().isEmpty() && (type.getValue() != null)) {
                controller.addParameterToPhase(id, parameter.getText(), type.getValue());
            }
            stage.hide();
        });

        stage.showAndWait();
    }
}
