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
    private Label phaseTypeLabel;
    private Label Name;

    private Stage mainWindow;
    private Stage stepDetails = new Stage();

    private BorderPane root = new BorderPane();
    private GridPane detailsContainer = new GridPane();
    private HBox bottomContainer = new HBox();
    private HBox labelHBox = new HBox();
    private Scene scene = new Scene(root);

    private DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
    
    private Map<String, Color> colors = new HashMap();
    private Color selectedColor;
    private boolean tool;
    private int detailedContainerX = 0;
    private int detailedContainerY = 0;
    private StepModel model;
    
    private String var = "";
    private Tooltip tip;
    
    private final LocalTime time = LocalTime.now();

    private final RecipeEditorController controller;

    public Step(String text,boolean tool, Stage window) {
        model = new StepModel(text);
        this.controller = ApplicationContext.applicationContext.getBean(RecipeEditorController.class);
        
        List<Phase> list = controller.getAllPhases();
        list.add(new Phase(-1L, "Start", "", "Start",null));
        list.add(new Phase(-1L, "End", "", "End", null));
        list.stream()
                .filter(phase -> phase.getName().equals(text))
                .findAny()
                .ifPresent(type -> {
                    colors.put(PhasesTypes.Dose_phase.name().replace("_", " ").trim(), Color.GREEN);
                    colors.put(PhasesTypes.Circulating_Phase.name().replace("_", " ").trim(), Color.OLIVE);
                    colors.put(PhasesTypes.Washing_phase.name().replace("_", " ").trim(), Color.ORANGERED);
                    colors.put(PhasesTypes.Transfere_phase.name().replace("_", " ").trim(), Color.DARKBLUE);
                    colors.put(PhasesTypes.Mixing_phase.name().replace("_", " ").trim(), Color.DARKSALMON);
                    colors.put("Start", Color.BLACK);
                    colors.put("End", Color.BLACK);
                    phaseTypeLabel = new Label(type.getPhaseType());
                    if (type.getName().equals("Start") || type.getName().equals("End")) {
                        Name = new Label("");
                    } else {
                        Name = new Label(type.getName());
                    }
                    this.mainWindow = window;
                    this.tool = tool;
                    initialization(type.getPhaseType());
                });
    }

    private void initialization(String type) {
        
        selectedColor = colors.get(type);
        setBackground(new Background(new BackgroundFill(selectedColor, CornerRadii.EMPTY, Insets.EMPTY)));
        setEffect(shadow);
        getChildren().addAll(phaseTypeLabel, labelHBox);
        setPrefSize(300, 60);
        setMinWidth(225);
        setSpacing(5);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
        setPadding(new Insets(10));
        setOnMouseClicked(action -> {
            if (model != null) {
                fillParametersInConfigurationWindow();

                if (tool && action.getClickCount() == 2) {
                    stepDetails.setX(action.getScreenX());
                    stepDetails.setY(action.getScreenY());
                    stepDetails.show();
                }
            } else {
                Alert Error = new Alert(Alert.AlertType.ERROR);
                Error.setTitle("Error ");
                Error.setHeaderText("Error Step data");
                Error.setContentText("Error in step model data ...");
                Error.initOwner(mainWindow);
                Error.showAndWait();
            }
        });
        
        setOnMouseEntered(event -> {
            Tooltip.uninstall(this, tip);
            tip = new Tooltip(getTooltipString());
            Tooltip.install(this, tip);
            setBackground(new Background(new BackgroundFill(selectedColor.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        });
        setOnMouseExited(action -> setBackground(new Background(new BackgroundFill(selectedColor.brighter(), CornerRadii.EMPTY, Insets.EMPTY))));
        
        createConfigurationWindow();
    }
    
    private void createConfigurationWindow(){

        root.setCenter(detailsContainer);
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        root.setPadding(new Insets(5));
        root.setBottom(bottomContainer);
        root.setPrefWidth(500);

        detailsContainer.setVgap(5);
        detailsContainer.setHgap(5);
        detailsContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        detailsContainer.setPadding(new Insets(10));
        

        bottomContainer.setSpacing(10);
        bottomContainer.setPadding(new Insets(5));
        bottomContainer.setAlignment(Pos.CENTER);

        stepDetails.initOwner(mainWindow);
        stepDetails.initModality(Modality.WINDOW_MODAL);
        stepDetails.initStyle(StageStyle.UTILITY);
        stepDetails.setScene(scene);
        stepDetails.setTitle(Name.getText());
        stepDetails.setResizable(false);
        
        labelHBox.getChildren().addAll(Name);
        labelHBox.setSpacing(5);
        labelHBox.setAlignment(Pos.CENTER);

        phaseTypeLabel.setStyle("-fx-font-weight:bold;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:16;");
        Name.setStyle("-fx-font-weight:normal;-fx-font-style:normal;-fx-text-fill:white;-fx-font-size:16;");
    }
    
    private void fillParametersInConfigurationWindow() {
        if (model != null) {
            detailsContainer.getChildren().clear();
            Map<String, Boolean> checksValues = model.getCheckParametersData();
            Map<String, Double> AnalogValues = model.getValueParametersData();
            List<Parameter> types = model.getParametersType();
            
            types.stream().sorted((Parameter t, Parameter t1) -> t1.getType().compareTo(t.getType())).forEach(parameter ->  {
                if (parameter.getType().equals(PhaseParameterType.Check.name())) {
                    CheckBox box = new CheckBox(parameter.getName());
                    box.setSelected(checksValues.get(parameter.getName()));
                    box.selectedProperty().addListener((observable, oldValue, newValue) -> checksValues.replace(parameter.getName(), newValue));
                    box.setPrefWidth(400);
                    detailsContainer.add(box, detailedContainerX, detailedContainerY++);
                    
                } else if (parameter.getType().equals(PhaseParameterType.Value.name())) {
                    Label label = new Label(parameter.getName());
                    label.setPrefWidth(150);
                    TextField field = new TextField(String.valueOf(AnalogValues.get(parameter.getName())));
                    field.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
                    field.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        if (KeyCode.ENTER.equals(event.getCode())) {
                            try {
                                AnalogValues.replace(parameter.getName(), Double.parseDouble(field.getText()));
                                field.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                            } catch (Exception e) {
                                field.setBackground(new Background(new BackgroundFill(Color.ORANGERED, CornerRadii.EMPTY, Insets.EMPTY)));
                            }
                        }
                    });
                    field.textProperty().addListener((observable, oldValue, newValue) -> field.setBackground(new Background(new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY))));
                    field.setPrefWidth(350);
                    detailsContainer.add(label, detailedContainerX, detailedContainerY);
                    detailsContainer.add(field, detailedContainerX + 1, detailedContainerY++);
                }
            });

            if (phaseTypeLabel.getText().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())) {
                Label label = new Label("Material name ");
                label.setPrefWidth(150);
                ComboBox<Material> field = new ComboBox<>();
                field.getItems().addAll(controller.getAllMaterials());
                field.setStyle("-fx-font-family: monospace;-fx-font-size: 12px;");
                field.valueProperty().addListener((observable, oldValue, newValue) -> model.setMaterialID(newValue.getId()));
                controller.getMaterialById(model.getMaterialID()).ifPresent(material -> {
                    field.getSelectionModel().select(material);//TODO - check'
                });
                field.setPrefWidth(350);
                detailsContainer.add(label, detailedContainerX, detailedContainerY);
                detailsContainer.add(field, detailedContainerX + 1, detailedContainerY++);
            }
        }
    }
    
    public String getStepName() {
        return Name.getText();
    }   

    public StepModel getModel() {
        return model;
    }

    public void setModel(StepModel model) {
        this.model = model;
    }
    
    private String getTooltipString() {
        var = "";
        if (model.getPhaseName().equals("Start") || model.getPhaseName().equals("End")) {
            var += "Phase name = " + model.getPhaseName() + "\n";
        } else {
            var += "Phase type = " + model.getPhaseType() + "\n";
            var += "Phase name = " + model.getPhaseName() + "\n";
            model.getParametersType().forEach(parameter -> {
                if (parameter.getType().equals(PhaseParameterType.Check.name())) {
                    var += parameter.getName() + " = " + model.getCheckParametersData().get(parameter.getName()) + "\n";
                } else if (parameter.getType().equals(PhaseParameterType.Value.name())) {
                    var += parameter.getName() + " = " + model.getValueParametersData().get(parameter.getName()) + "\n";
                }
            });
            if (phaseTypeLabel.getText().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())) {
                controller.getMaterialById(model.getMaterialID()).ifPresentOrElse(material -> {
                    var += "Material = " + material + "\n";
                }, () -> {
                    var += "Material = Material not found";
                });
            }
        }
        return var;
    }

    @Override
    public String toString() {
        return getStepName();
    }
}
