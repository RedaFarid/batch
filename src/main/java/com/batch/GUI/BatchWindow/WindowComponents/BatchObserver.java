

package com.batch.GUI.BatchWindow.WindowComponents;

import com.batch.ApplicationContext;
import com.batch.DTO.BatchSystemDataDefinitions.BatchOrders;
import com.batch.DTO.BatchSystemDataDefinitions.BatchParallelStepsModel;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStates;
import com.batch.DTO.BatchSystemDataDefinitions.BatchStepModel;
import com.batch.DTO.BatchSystemDataDefinitions.EventOnBatchCloseCallBack;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.BatchControllerData;
import com.batch.GUI.BatchWindow.BatchesController;
import com.batch.GUI.BatchWindow.BatchesModel;
import com.batch.GUI.RecipeEditor.WindowComponents.ParallelSteps;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ExceptionDialog;

public class BatchObserver extends Tab {
    private static volatile BatchObserver singleton = null;
    private BorderPane rootPane = new BorderPane();
    private Stage mainWindow;
    private ToolBar toolBar = new ToolBar();
    private ToolBar statusBar = new ToolBar();
    private Label batchState = new Label();
    private StringProperty batchMode = new SimpleStringProperty();
    private final Button start = new Button("Start");
    private final Button abort = new Button("Abort");
    private final Button hold = new Button("Hold");
    private final Button resume = new Button("Resume");
    private final Button close = new Button("Close");
    private final Button terminate = new Button("Terminate");
    private final HBox mainContainer = new HBox();
    private final VBox pane = new VBox();
    private final VBox piePane = new VBox();
    private final ScrollPane scrollPane;
    private final Batch batch;
    private EventOnBatchCloseCallBack eventOnClose;
    private int i;
    private int j;
    private final BatchesController controller;
    private final BatchesModel model;
    private final Background IDLE_FILL;
    private final Background CREATED_FILL;
    private final Background RUNNING_FILL;
    private final Background HELD_FILL;
    private final Background ABORTED_FILL;
    private final Background FINISHED_FILL;

    public BatchObserver(Stage stage, Batch batch) {
        this.scrollPane = new ScrollPane(this.pane);
        this.IDLE_FILL = new Background(new BackgroundFill[]{new BackgroundFill(Color.GRAY, new CornerRadii((double)5.0F), Insets.EMPTY)});
        this.CREATED_FILL = new Background(new BackgroundFill[]{new BackgroundFill(Color.DARKGRAY, new CornerRadii((double)5.0F), Insets.EMPTY)});
        this.RUNNING_FILL = new Background(new BackgroundFill[]{new BackgroundFill(Color.LIGHTGREEN, new CornerRadii((double)5.0F), Insets.EMPTY)});
        this.HELD_FILL = new Background(new BackgroundFill[]{new BackgroundFill(Color.YELLOW, new CornerRadii((double)5.0F), Insets.EMPTY)});
        this.ABORTED_FILL = new Background(new BackgroundFill[]{new BackgroundFill(Color.VIOLET, new CornerRadii((double)5.0F), Insets.EMPTY)});
        this.FINISHED_FILL = new Background(new BackgroundFill[]{new BackgroundFill(Color.DARKGREEN, new CornerRadii((double)5.0F), Insets.EMPTY)});
        this.mainWindow = stage;
        this.batch = batch;
        this.controller = (BatchesController)ApplicationContext.applicationContext.getBean(BatchesController.class);
        this.model = this.controller.getModel();
        this.graphicsBuilder();
        this.actionHandler();
        this.LoadRecipeToGraphicsWithoutEdit();
        this.createPieChart();
    }

    public static BatchObserver getWindow(Stage stage, Batch batch) {
        synchronized(BatchObserver.class) {
            if (singleton == null) {
                singleton = new BatchObserver(stage, batch);
            }
        }

        return singleton;
    }

    private void graphicsBuilder() {
        this.mainContainer.getChildren().addAll(new Node[]{this.piePane, this.scrollPane});
        this.batchState.setPrefWidth((double)300.0F);
        this.batchState.setPadding(new Insets((double)10.0F));
        this.batchState.setAlignment(Pos.CENTER);
        this.start.setPrefWidth((double)150.0F);
        this.abort.setPrefWidth((double)150.0F);
        this.hold.setPrefWidth((double)150.0F);
        this.resume.setPrefWidth((double)150.0F);
        this.close.setPrefWidth((double)150.0F);
        this.terminate.setPrefWidth((double)250.0F);
        this.scrollPane.prefWidthProperty().bind(this.rootPane.widthProperty().divide(3).multiply(2));
        this.scrollPane.setStyle("-fx-background-color:white; -fx-focus-color: white;-fx-control-inner-background:white;");
        this.scrollPane.setBorder(new Border(new BorderStroke[]{new BorderStroke(Color.WHITE, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths((double)0.0F))}));
        this.pane.setAlignment(Pos.CENTER);
        this.pane.setSpacing((double)5.0F);
        this.pane.setPadding(new Insets((double)20.0F));
        this.pane.prefWidthProperty().bind(this.scrollPane.widthProperty().subtract(20));
        this.piePane.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)}));
        this.piePane.setAlignment(Pos.CENTER);
        this.piePane.setSpacing((double)5.0F);
        this.piePane.setPadding(new Insets((double)20.0F));
        this.piePane.prefHeightProperty().bind(this.rootPane.heightProperty());
        this.piePane.prefWidthProperty().bind(this.rootPane.widthProperty().divide(3));
        this.toolBar.getItems().addAll(new Node[]{this.start, this.hold, this.resume, this.abort, new Separator(), this.close, new Separator(), this.terminate, new Separator(), this.batchState});
        this.statusBar.getItems().addAll(new Node[]{new Label("Batch interfece status")});
        this.rootPane.setCenter(this.mainContainer);
        this.rootPane.setTop(this.toolBar);
        this.rootPane.setBottom(this.statusBar);
        this.setClosable(false);
        String var10001 = this.batch.getBatchName();
        this.setText("Batch : " + var10001 + "  On [" + this.batch.getUnitName() + "]");
        this.setContent(this.rootPane);
    }

    private void actionHandler() {
        this.resume.setOnMouseClicked(this::onResumeBatch);
        this.start.setOnMouseClicked(this::onStartBatch);
        this.abort.setOnMouseClicked(this::onAbortBatch);
        this.hold.setOnMouseClicked(this::onHoldBatch);
        this.close.setOnMouseClicked(this::onCloseBatch);
        this.terminate.setOnMouseClicked(this::onTerminateBatch);
        this.batchMode.addListener(this::onModeChange);
    }

    private void onResumeBatch(MouseEvent action) {
        try {
            this.controller.getBatchControllerDataForUnit(this.batch.getUnitName()).ifPresentOrElse((batchControllerData) -> {
                if (batchControllerData.getRunningBatchID() == this.batch.getId()) {
                    this.batch.setOrder(BatchOrders.Resume.name());
                    this.controller.controlWholeBatch(this.batch.getUnitName(), this.batch.getId(), BatchOrders.Resume.name());
                } else {
                    this.showPopupWindow(AlertType.ERROR, "Error ", "Error resuming the Batch", "Please close the running batch first");
                }

            }, () -> this.showPopupWindow(AlertType.ERROR, "Error ", "Error resuming the Batch", "Please close the running batch first"));
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onStartBatch(MouseEvent action) {
        try {
            this.showPopupWindowWithReturn(AlertType.CONFIRMATION, "Confirm", "Starting batch", "Confirm to start the batch").ifPresent((buttonType) -> {
                if (buttonType.equals(ButtonType.OK)) {
                    this.controller.getBatchControllerDataForUnit(this.batch.getUnitName()).ifPresentOrElse((batchControllerData) -> {
                        if (batchControllerData.getRunningBatchID() == this.batch.getId()) {
                            this.batch.setOrder(BatchOrders.Start.name());
                            this.controller.controlWholeBatch(this.batch.getUnitName(), this.batch.getId(), BatchOrders.Start.name());
                            this.showPopupWindow(AlertType.INFORMATION, "Batch info", "Loading batch ...", "Batch loaded and started successfully");
                        } else if (batchControllerData.getRunningBatchID() == 0L) {
                            this.batch.setOrder(BatchOrders.Start.name());
                            this.controller.UpdateBatchControlOrder(this.batch.getId(), BatchOrders.Start.name());
                            this.controller.updateBatchControllerData(new BatchControllerData(this.batch.getUnitName(), this.batch.getId(), 0, false, false));
                            this.showPopupWindow(AlertType.INFORMATION, "Batch info", "Loading batch ...", "Batch loaded and started successfully");
                        } else {
                            this.showPopupWindow(AlertType.ERROR, "Error ", "Error starting new Batch", "Please close the running batch first");
                        }

                    }, () -> {
                        this.batch.setOrder(BatchOrders.Start.name());
                        this.controller.UpdateBatchControlOrder(this.batch.getId(), BatchOrders.Start.name());
                        this.controller.createBatchControllerData(new BatchControllerData(this.batch.getUnitName(), this.batch.getId(), 0, false, false));
                        this.showPopupWindow(AlertType.INFORMATION, "Batch info", "Loading batch ...", "Batch loaded and started successfully");
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onAbortBatch(MouseEvent action) {
        try {
            this.showPopupWindowWithReturn(AlertType.CONFIRMATION, "Confirm", "Aborting batch", "Confirm to abort the batch").ifPresent((buttonType) -> {
                if (buttonType.equals(ButtonType.OK)) {
                    this.controller.getBatchControllerDataForUnit(this.batch.getUnitName()).ifPresentOrElse((batchControllerData) -> {
                        if (batchControllerData.getRunningBatchID() == this.batch.getId()) {
                            this.batch.setOrder(BatchOrders.Abort.name());
                            this.controller.UpdateBatchControlOrder(this.batch.getId(), BatchOrders.Abort.name());
                            this.controller.updateLockGeneralControl(false, this.batch.getUnitName());
                        } else {
                            this.showPopupWindow(AlertType.ERROR, "Error ", "Error aborting the Batch", "Please close the running batch first");
                        }

                    }, () -> this.showPopupWindow(AlertType.ERROR, "Error ", "Error aborting the Batch", "Please start the new batch first"));
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onHoldBatch(MouseEvent action) {
        try {
            this.controller.getBatchControllerDataForUnit(this.batch.getUnitName()).ifPresentOrElse((batchControllerData) -> {
                if (batchControllerData.getRunningBatchID() == this.batch.getId()) {
                    this.batch.setOrder(BatchOrders.Hold.name());
                    this.controller.UpdateBatchControlOrder(this.batch.getId(), BatchOrders.Hold.name());
                    this.controller.updateLockGeneralControl(false, this.batch.getUnitName());
                } else {
                    this.showPopupWindow(AlertType.ERROR, "Error ", "Error holding the Batch", "Please close the running batch first");
                }

            }, () -> this.showPopupWindow(AlertType.ERROR, "Error ", "Error aborting the Batch", "Please start the new batch first"));
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onCloseBatch(MouseEvent action) {
        try {
            this.showPopupWindowWithReturn(AlertType.CONFIRMATION, "Confirm", "Aborting batch", "Confirm to abort the batch").ifPresent((buttonType) -> {
                if (buttonType.equals(ButtonType.OK)) {
                    this.controller.getBatchControllerDataForUnit(this.batch.getUnitName()).ifPresentOrElse((batchControllerData) -> {
                        if (!this.batch.getState().equals(BatchStates.Idle.name()) && !this.batch.getState().equals(BatchStates.Finished.name())) {
                            this.showPopupWindow(AlertType.ERROR, "Error ", "Error closing the Batch", "Please finish the running batch first or abort it");
                        } else {
                            this.batch.setOrder(BatchOrders.Close.name());
                            this.controller.closeBatch(this.batch);
                            this.eventOnClose.Action(this);
                            this.showPopupWindow(AlertType.INFORMATION, "Batch info ", "Closing batch ...", "Batch will be transferred to archive");
                        }

                    }, () -> this.showPopupWindow(AlertType.ERROR, "Error ", "Error closing the Batch", "Batch not found,\nPlease start the new batch first"));
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onTerminateBatch(MouseEvent action) {
        try {
            this.showPopupWindowWithReturn(AlertType.CONFIRMATION, "Confirm", "Terminating batch", "Confirm to terminate the batch").ifPresent((buttonType) -> {
                if (buttonType.equals(ButtonType.OK)) {
                    this.controller.getBatchControllerDataForUnit(this.batch.getUnitName()).ifPresentOrElse((data) -> {
                        data.setCurrentParallelStepsNo(0);
                        data.setRunningBatchID(0L);
                        data.setLockGeneralControl(false);
                        data.setControlBit(false);
                        this.controller.updateBatchControllerData(data);
                        this.batch.setOrder(BatchOrders.Close.name());
                        this.batch.setState(BatchStates.Idle.name());
                        this.controller.updateBatch(this.batch);
                        this.showPopupWindow(AlertType.WARNING, "Warning ", "Terminating batch", "Batch terminated successfully");
                        this.eventOnClose.Action(this);
                    }, () -> this.showPopupWindow(AlertType.ERROR, "Error ", "Error terminating the Batch", "Please finish the running batch first or abort it"));
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorWindowForException(e.getMessage(), e);
        }

    }

    private void onModeChange(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        switch (newValue) {
            case "Idle":
                this.batchState.setText(newValue);
                this.batchState.setBackground(this.IDLE_FILL);
                this.start.setDisable(false);
                this.abort.setDisable(false);
                this.hold.setDisable(true);
                this.resume.setDisable(false);
                this.close.setDisable(true);
                this.terminate.setDisable(false);
                break;
            case "Created":
                this.batchState.setText(newValue);
                this.batchState.setBackground(this.CREATED_FILL);
                this.start.setDisable(false);
                this.abort.setDisable(true);
                this.hold.setDisable(true);
                this.resume.setDisable(true);
                this.close.setDisable(true);
                this.terminate.setDisable(false);
                break;
            case "Running":
                this.batchState.setText(newValue);
                this.batchState.setBackground(this.RUNNING_FILL);
                this.start.setDisable(true);
                this.abort.setDisable(false);
                this.hold.setDisable(false);
                this.resume.setDisable(true);
                this.close.setDisable(true);
                this.terminate.setDisable(false);
                break;
            case "Held":
                this.batchState.setText(newValue);
                this.batchState.setBackground(this.HELD_FILL);
                this.start.setDisable(true);
                this.abort.setDisable(true);
                this.hold.setDisable(true);
                this.resume.setDisable(false);
                this.close.setDisable(true);
                this.terminate.setDisable(false);
                break;
            case "Aborted":
                this.batchState.setText(newValue);
                this.batchState.setBackground(this.ABORTED_FILL);
                this.start.setDisable(true);
                this.abort.setDisable(true);
                this.hold.setDisable(true);
                this.resume.setDisable(true);
                this.close.setDisable(true);
                this.terminate.setDisable(false);
                break;
            case "Finished":
                this.batchState.setText(newValue);
                this.batchState.setBackground(this.FINISHED_FILL);
                this.start.setDisable(true);
                this.abort.setDisable(true);
                this.hold.setDisable(true);
                this.resume.setDisable(true);
                this.close.setDisable(false);
                this.terminate.setDisable(false);
        }

    }

    private void LoadRecipeToGraphicsWithoutEdit() {
        try {
            int stepNo = 0;
            int parallelStepNo = 0;
            this.pane.getChildren().clear();

            for(BatchParallelStepsModel pSM : this.batch.getModel().getParallelSteps()) {
                ParallelSteps parallelStepTemp = new ParallelSteps();
                this.pane.getChildren().add(parallelStepTemp);
                stepNo = 0;

                for(BatchStepModel sm : pSM.getSteps()) {
                    BatchStep step = new BatchStep(this.batch.getId(), parallelStepNo, stepNo, sm, sm.getPhaseName(), this.mainWindow);
                    parallelStepTemp.getChildren().add(step);
                    ++stepNo;
                }

                ++parallelStepNo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createPieChart() {
        try {
            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
            this.batch.getModel().getParallelSteps().stream().flatMap((item) -> item.getSteps().stream()).filter((item) -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())).forEach((item) -> this.controller.getMaterialByName(item.getMaterialID()).ifPresentOrElse((material) -> chartData.add(new PieChart.Data(material.getName(), (Double)item.getValueParametersData().get("Percentage %"))), () -> chartData.add(new PieChart.Data("Unknown", (Double)item.getValueParametersData().get("Percentage %")))));
            if (chartData.isEmpty()) {
                this.piePane.getChildren().add(new Label("Process batch, does not have any components"));
                return;
            }

            PieChart pieChart = new PieChart(chartData);
            pieChart.setAccessibleText("Batch components");
            pieChart.setLegendVisible(true);
            pieChart.setTitle("Batch components");
            this.piePane.getChildren().add(pieChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void update() {
        try {
            this.controller.getBatchByID(this.batch.getId()).ifPresent((loadedBatch) -> {
                this.updateOnlineBatchFromDatabaseBatch(this.batch, loadedBatch);
                Platform.runLater(this::updateBatch);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void updateBatch() {
        try {
            this.pane.getChildren().stream().map((parallelStepNode) -> (ParallelSteps)parallelStepNode).forEachOrdered((PS) -> PS.getChildren().stream().map((stepNode) -> (BatchStep)stepNode).forEachOrdered(BatchStep::run));
            this.batchMode.setValue(this.batch.getState());
            if (this.batch.getState().equals(BatchStates.Created.name())) {
                this.setStyle("-fx-background-color: DARKGRAY;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (this.batch.getState().equals(BatchStates.Idle.name())) {
                this.setStyle("-fx-background-color: GRAY;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (this.batch.getState().equals(BatchStates.Running.name())) {
                this.setStyle("-fx-background-color: LIGHTGREEN;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (this.batch.getState().equals(BatchStates.Held.name())) {
                this.setStyle("-fx-background-color: YELLOW;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (this.batch.getState().equals(BatchStates.Aborted.name())) {
                this.setStyle("-fx-background-color: VIOLET;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (this.batch.getState().equals(BatchStates.Finished.name())) {
                this.setStyle("-fx-background-color: DARKGREEN;-fx-border-color: darkblue; -fx-border-width:0.1;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateOnlineBatchFromDatabaseBatch(Batch batch, Batch loadedBatch) {
        try {
            if (loadedBatch != null) {
                batch.setState(loadedBatch.getState());

                for(this.i = 0; this.i < batch.getModel().getParallelSteps().size(); ++this.i) {
                    for(this.j = 0; this.j < ((BatchParallelStepsModel)batch.getModel().getParallelSteps().get(this.i)).getSteps().size(); ++this.j) {
                        ((BatchStepModel)((BatchParallelStepsModel)batch.getModel().getParallelSteps().get(this.i)).getSteps().get(this.j)).setState(((BatchStepModel)((BatchParallelStepsModel)loadedBatch.getModel().getParallelSteps().get(this.i)).getSteps().get(this.j)).getState());
                        Map<String, Boolean> checkActualData = ((BatchStepModel)((BatchParallelStepsModel)batch.getModel().getParallelSteps().get(this.i)).getSteps().get(this.j)).getActualCheckParametersData();
                        Map<String, Double> valueActualData = ((BatchStepModel)((BatchParallelStepsModel)batch.getModel().getParallelSteps().get(this.i)).getSteps().get(this.j)).getActualvalueParametersData();
                        checkActualData.forEach((paraName, paraValue) -> {
                            boolean value = (Boolean)((BatchStepModel)((BatchParallelStepsModel)loadedBatch.getModel().getParallelSteps().get(this.i)).getSteps().get(this.j)).getActualCheckParametersData().get(paraName);
                            checkActualData.replace(paraName, value);
                        });
                        valueActualData.forEach((paraName, paraValue) -> {
                            double value = (Double)((BatchStepModel)((BatchParallelStepsModel)loadedBatch.getModel().getParallelSteps().get(this.i)).getSteps().get(this.j)).getActualvalueParametersData().get(paraName);
                            valueActualData.replace(paraName, value);
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setOnBatchClose(EventOnBatchCloseCallBack event) {
        this.eventOnClose = event;
    }

    public long getBatchID() {
        return this.batch.getId();
    }

    private Optional<ButtonType> showPopupWindowWithReturn(Alert.AlertType type, String title, String header, String content) {
        Alert Error = new Alert(type);
        Error.setTitle(title);
        Error.setHeaderText(header);
        Error.setContentText(content);
        Error.initOwner(this.mainWindow);
        Error.initStyle(StageStyle.UTILITY);
        return Error.showAndWait();
    }

    private void showPopupWindow(Alert.AlertType type, String title, String header, String content) {
        Alert Error = new Alert(type);
        Error.setTitle(title);
        Error.setHeaderText(header);
        Error.setContentText(content);
        Error.initOwner(this.mainWindow);
        Error.initStyle(StageStyle.UTILITY);
        Error.showAndWait();
    }

    private void showErrorWindowForException(String header, Throwable e) {
        Platform.runLater(() -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
            exceptionDialog.setHeaderText(header);
            exceptionDialog.getDialogPane().setMaxWidth((double)500.0F);
            exceptionDialog.initOwner(this.mainWindow);
            exceptionDialog.initModality(Modality.WINDOW_MODAL);
            exceptionDialog.initStyle(StageStyle.UTILITY);
            exceptionDialog.show();
        });
    }
}
