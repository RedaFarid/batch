package com.batch.GUI.RecipeEditor.WindowComponents;

import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.PhaseParameterType;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.DTO.RecipeSystemDataDefinitions.StepModel;
import com.batch.Database.Entities.Material;
import com.batch.Database.Entities.Parameter;
import com.batch.Database.Entities.Phase;
import com.batch.GUI.RecipeEditor.RecipeEditorController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step extends VBox {
    private final LocalTime time;
    private final RecipeEditorController controller;
    private final Stage stepDetails = new Stage();
    private final BorderPane root = new BorderPane();
    private final GridPane detailsContainer = new GridPane();
    private final HBox bottomContainer = new HBox();
    private final HBox labelHBox = new HBox();
    private final Scene scene;
    private final DropShadow shadow;
    private final Map<String, Color> colors;
    private final int detailedContainerX;
    private Label phaseTypeLabel;
    private Label Name;
    private Stage mainWindow;
    private Color selectedColor;
    private boolean tool;
    private int detailedContainerY;
    private StepModel model;
    private String var;
    private Tooltip tip;

    public Step(String text, boolean tool, Stage window) {
        this.scene = new Scene(this.root);
        this.shadow = new DropShadow(5.0F, 3.0F, 3.0F, Color.GRAY);
        this.colors = new HashMap();
        this.detailedContainerX = 0;
        this.detailedContainerY = 0;
        this.var = "";
        this.time = LocalTime.now();
        this.model = new StepModel(text);
        this.controller = ApplicationContext.applicationContext.getBean(RecipeEditorController.class);
        List<Phase> list = this.controller.getAllPhases();
        list.add(new Phase(-1L, "Start", "", "Start", null));
        list.add(new Phase(-1L, "End", "", "End", null));
        list.stream().filter((phase) -> phase.getName().equals(text)).findAny().ifPresent((type) -> {
            this.colors.put(PhasesTypes.Dose_phase.name().replace("_", " ").trim(), Color.GREEN);
            this.colors.put(PhasesTypes.Circulating_Phase.name().replace("_", " ").trim(), Color.OLIVE);
            this.colors.put(PhasesTypes.Washing_phase.name().replace("_", " ").trim(), Color.ORANGERED);
            this.colors.put(PhasesTypes.Transfere_phase.name().replace("_", " ").trim(), Color.DARKBLUE);
            this.colors.put(PhasesTypes.Mixing_phase.name().replace("_", " ").trim(), Color.DARKSALMON);
            this.colors.put("Start", Color.BLACK);
            this.colors.put("End", Color.BLACK);
            this.phaseTypeLabel = new Label(type.getPhaseType());
            if (!type.getName().equals("Start") && !type.getName().equals("End")) {
                this.Name = new Label(type.getName());
            } else {
                this.Name = new Label("");
            }

            this.mainWindow = window;
            this.tool = tool;
            this.initialization(type.getPhaseType());
        });
    }

    private void initialization(String type) {
        this.selectedColor = this.colors.get(type);
        this.setBackground(new Background(new BackgroundFill(this.selectedColor, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setEffect(this.shadow);
        this.getChildren().addAll(this.phaseTypeLabel, this.labelHBox);
        this.setPrefSize(300.0F, 60.0F);
        this.setMinWidth(225.0F);
        this.setSpacing(5.0F);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10.0F));
        this.setPadding(new Insets(10.0F));
        this.setOnMouseClicked((action) -> {
            if (this.model != null) {
                this.fillParametersInConfigurationWindow();
                if (this.tool && action.getClickCount() == 2) {
                    this.stepDetails.setX(action.getScreenX());
                    this.stepDetails.setY(action.getScreenY());
                    this.stepDetails.show();
                }
            } else {
                Alert Error = new Alert(AlertType.ERROR);
                Error.setTitle("Error ");
                Error.setHeaderText("Error Step data");
                Error.setContentText("Error in step model data ...");
                Error.initOwner(this.mainWindow);
                Error.showAndWait();
            }

        });
        this.setOnMouseEntered((event) -> {
            Tooltip.uninstall(this, this.tip);
            this.tip = new Tooltip(this.getTooltipString());
            Tooltip.install(this, this.tip);
            this.setBackground(new Background(new BackgroundFill(this.selectedColor.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        });
        this.setOnMouseExited((action) -> this.setBackground(new Background(new BackgroundFill(this.selectedColor.brighter(), CornerRadii.EMPTY, Insets.EMPTY))));
        this.createConfigurationWindow();
    }

    private void createConfigurationWindow() {
        this.root.setCenter(this.detailsContainer);
        this.root.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        this.root.setPadding(new Insets(5.0F));
        this.root.setBottom(this.bottomContainer);
        this.root.setPrefWidth(500.0F);
        this.detailsContainer.setVgap(5.0F);
        this.detailsContainer.setHgap(5.0F);
        this.detailsContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        this.detailsContainer.setPadding(new Insets(10.0F));
        this.bottomContainer.setSpacing(10.0F);
        this.bottomContainer.setPadding(new Insets(5.0F));
        this.bottomContainer.setAlignment(Pos.CENTER);
        this.stepDetails.initOwner(this.mainWindow);
        this.stepDetails.initModality(Modality.WINDOW_MODAL);
        this.stepDetails.initStyle(StageStyle.UTILITY);
        this.stepDetails.setScene(this.scene);
        this.stepDetails.setTitle(this.Name.getText());
        this.stepDetails.setResizable(false);
        this.labelHBox.getChildren().addAll(this.Name);
        this.labelHBox.setSpacing(5.0F);
        this.labelHBox.setAlignment(Pos.CENTER);
        this.phaseTypeLabel.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:16;");
        this.Name.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:16;");
    }

    private void fillParametersInConfigurationWindow() {
        if (this.model != null) {
            this.detailsContainer.getChildren().clear();
            Map<String, Boolean> checksValues = this.model.getCheckParametersData();
            Map<String, Double> AnalogValues = this.model.getValueParametersData();
            List<Parameter> types = this.model.getParametersType();
            types.stream().sorted((t, t1) -> t1.getType().compareTo(t.getType())).forEach((parameter) -> {
                if (parameter.getType().equals(PhaseParameterType.Check.name())) {
                    CheckBox box = new CheckBox(parameter.getName());
                    box.setSelected(checksValues.get(parameter.getName()));
                    box.selectedProperty().addListener((observable, oldValue, newValue) -> checksValues.replace(parameter.getName(), newValue));
                    box.setPrefWidth(400.0F);
                    this.detailsContainer.add(box, this.detailedContainerX, this.detailedContainerY++);
                } else if (parameter.getType().equals(PhaseParameterType.Value.name())) {
                    Label label = new Label(parameter.getName());
                    label.setPrefWidth(150.0F);
                    TextField field = new TextField(String.valueOf(AnalogValues.get(parameter.getName())));
                    field.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    field.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
                        if (KeyCode.ENTER.equals(event.getCode())) {
                            try {
                                AnalogValues.replace(parameter.getName(), Double.parseDouble(field.getText()));
                                field.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                            } catch (Exception var5) {
                                field.setBackground(new Background(new BackgroundFill(Color.ORANGERED, CornerRadii.EMPTY, Insets.EMPTY)));
                            }
                        }

                    });
                    field.textProperty().addListener((observable, oldValue, newValue) -> field.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY))));
                    field.setPrefWidth(350.0F);
                    this.detailsContainer.add(label, this.detailedContainerX, this.detailedContainerY);
                    this.detailsContainer.add(field, this.detailedContainerX + 1, this.detailedContainerY++);
                }

            });
            if (this.phaseTypeLabel.getText().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())) {
                Label label = new Label("Material name ");
                label.setPrefWidth(150.0F);
                ComboBox<Material> field = new ComboBox();
                field.getItems().addAll(this.controller.getAllMaterials());
                field.setStyle("-fx-font-family: monospace;-fx-font-size: 12px;");
                field.valueProperty().addListener((observable, oldValue, newValue) -> this.model.setMaterialID(newValue.getId()));
                this.controller.getMaterialById(this.model.getMaterialID()).ifPresent((material) -> field.getSelectionModel().select(material));
                field.setPrefWidth(350.0F);
                this.detailsContainer.add(label, this.detailedContainerX, this.detailedContainerY);
                this.detailsContainer.add(field, this.detailedContainerX + 1, this.detailedContainerY++);
            }
        }

    }

    public String getStepName() {
        return this.Name.getText();
    }

    public StepModel getModel() {
        return this.model;
    }

    public void setModel(StepModel model) {
        this.model = model;
    }

    private String getTooltipString() {
        this.var = "";
        if (!this.model.getPhaseName().equals("Start") && !this.model.getPhaseName().equals("End")) {
            String var1 = this.var;
            this.var = var1 + "Phase type = " + this.model.getPhaseType() + "\n";
            var1 = this.var;
            this.var = var1 + "Phase name = " + this.model.getPhaseName() + "\n";
            this.model.getParametersType().forEach((parameter) -> {
                if (parameter.getType().equals(PhaseParameterType.Check.name())) {
                    this.var = this.var + parameter.getName() + " = " + this.model.getCheckParametersData().get(parameter.getName()) + "\n";
                } else if (parameter.getType().equals(PhaseParameterType.Value.name())) {
                    this.var = this.var + parameter.getName() + " = " + this.model.getValueParametersData().get(parameter.getName()) + "\n";
                }

            });
            if (this.phaseTypeLabel.getText().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())) {
                this.controller.getMaterialById(this.model.getMaterialID()).ifPresentOrElse((material) -> this.var = this.var + "Material = " + material + "\n", () -> this.var = this.var + "Material = Material not found");
            }
        } else {
            String var10001 = this.var;
            this.var = var10001 + "Phase name = " + this.model.getPhaseName() + "\n";
        }

        return this.var;
    }

    public String toString() {
        return this.getStepName();
    }
}
