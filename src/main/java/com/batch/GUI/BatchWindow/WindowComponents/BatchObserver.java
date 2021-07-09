package com.batch.GUI.BatchWindow.WindowComponents;


import com.batch.ApplicationContext;
import com.batch.DTO.BatchSystemDataDefinitions.*;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.BatchControllerData;
import com.batch.GUI.BatchWindow.BatchesController;
import com.batch.GUI.BatchWindow.BatchesModel;
import com.batch.GUI.RecipeEditor.WindowComponents.ParallelSteps;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.Map;

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
    private final ScrollPane scrollPane = new ScrollPane(pane);

    private final Batch batch;
    private EventOnBatchCloseCallBack eventOnClose;

    private int i, j;

    private final BatchesController controller;
    private final BatchesModel model;

    public BatchObserver(Stage stage, Batch batch) {
        this.mainWindow = stage;
        this.batch = batch;
        this.controller = ApplicationContext.applicationContext.getBean(BatchesController.class);
        this.model = controller.getModel();
        graphicsBuilder();
        actionHandler();
        LoadRecipeToGraphicsWithoutEdit();
        createPieChart();
    }

    public static BatchObserver getWindow(Stage stage, Batch batch) {
        synchronized (BatchObserver.class) {
            if (singleton == null) {
                singleton = new BatchObserver(stage, batch);
            }
        }
        return singleton;
    }

    private void graphicsBuilder() {

        mainContainer.getChildren().addAll(piePane, scrollPane);
        
        batchState.setPrefWidth(300);
        batchState.setPadding(new Insets(10));
        batchState.setAlignment(Pos.CENTER);

        start.setPrefWidth(150);
        abort.setPrefWidth(150);
        hold.setPrefWidth(150);
        resume.setPrefWidth(150);
        close.setPrefWidth(150);
        terminate.setPrefWidth(250);
        
        scrollPane.prefWidthProperty().bind(rootPane.widthProperty().divide(3).multiply(2));
        scrollPane.setStyle("-fx-background-color:white; -fx-focus-color: white;-fx-control-inner-background:white;");
        scrollPane.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(0))));

        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(5);
        pane.setPadding(new Insets(20));
        pane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        
        piePane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        piePane.setAlignment(Pos.CENTER);
        piePane.setSpacing(5);
        piePane.setPadding(new Insets(20));
        piePane.prefHeightProperty().bind(rootPane.heightProperty());
        piePane.prefWidthProperty().bind(rootPane.widthProperty().divide(3));

        toolBar.getItems().addAll(start, hold, resume, abort, new Separator(), close, new Separator(), terminate, new Separator(), batchState);

        statusBar.getItems().addAll(new Label("Batch interfece status"));

        rootPane.setCenter(mainContainer);
        rootPane.setTop(toolBar);
        rootPane.setBottom(statusBar);

        setClosable(false);
        setText("Batch : " + batch.getBatchName() + "  On [" + batch.getUnitName() + "]");
        setContent(rootPane);

    }
    private void actionHandler() {
        resume.setOnMouseClicked(this::onResumeBatch);
        start.setOnMouseClicked(this::onStartBatch);
        abort.setOnMouseClicked(this::onAbortBatch);
        hold.setOnMouseClicked(this::onHoldBatch);
        close.setOnMouseClicked(this::onCloseBatch);
        terminate.setOnMouseClicked(this::onTerminateBatch);
        batchMode.addListener(this::onModeChange);
    }

    private void onResumeBatch(MouseEvent action) {
        try {
            controller.getBatchControllerDataForUnit(batch.getUnitName()).ifPresentOrElse(batchControllerData -> {
                if (batchControllerData.getRunningBatchID() == batch.getId()) {
                    batch.setOrder(BatchOrders.Resume.name());
                    controller.controlWholeBatch(batch.getUnitName(), batch.getId(), BatchOrders.Resume.name());
                }else {
                    showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error resuming the Batch", "Please close the running batch first");
                }
            }, () -> {
                showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error resuming the Batch", "Please close the running batch first");
            });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }
    private void onStartBatch(MouseEvent action) {
        try {
            controller.getBatchControllerDataForUnit(batch.getUnitName()).ifPresentOrElse(batchControllerData -> {
                if (batchControllerData.getRunningBatchID() == batch.getId()) {
                    batch.setOrder(BatchOrders.Start.name());
                    controller.controlWholeBatch(batch.getUnitName(), batch.getId(), BatchOrders.Start.name());
                    controller.updateLockGeneralControl(false, batch.getUnitName());
                } else if (batchControllerData.getRunningBatchID() == 0) {
                    batch.setOrder(BatchOrders.Start.name());
                    controller.UpdateBatchControlOrder(batch.getId(), BatchOrders.Start.name());
                    controller.updateBatchControllerData(new BatchControllerData(batch.getUnitName(), batch.getId(), 0, false, false));
                    showPopupWindow(Alert.AlertType.INFORMATION, "Batch info", "Loading batch ...", "Batch will be loaded and started");
                } else {
                    showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error starting new Batch", "Please close the running batch first");
                }
            }, () -> {
                batch.setOrder(BatchOrders.Start.name());
                controller.UpdateBatchControlOrder(batch.getId(), BatchOrders.Start.name());
                controller.createBatchControllerData(new BatchControllerData(batch.getUnitName(), batch.getId(), 0, false, false));
                showPopupWindow(Alert.AlertType.INFORMATION, "Batch info", "Loading batch ...", "Batch will be loaded and started");
            });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }
    private void onAbortBatch(MouseEvent action) {
        try {
            controller.getBatchControllerDataForUnit(batch.getUnitName()).ifPresentOrElse(batchControllerData -> {
                if (batchControllerData.getRunningBatchID() == batch.getId()) {
                    batch.setOrder(BatchOrders.Abort.name());
                    controller.UpdateBatchControlOrder(batch.getId(), BatchOrders.Abort.name());
                    controller.updateLockGeneralControl(false, batch.getUnitName());
                } else {
                    showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error aborting the Batch", "Please close the running batch first");
                }
            }, () -> {
                showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error aborting the Batch", "Please start the new batch first");
            });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }
    private void onHoldBatch(MouseEvent action) {
        try {
            controller.getBatchControllerDataForUnit(batch.getUnitName()).ifPresentOrElse(batchControllerData -> {
                if (batchControllerData.getRunningBatchID() == batch.getId()) {
                    batch.setOrder(BatchOrders.Hold.name());
                    controller.UpdateBatchControlOrder(batch.getId(), BatchOrders.Hold.name());
                    controller.updateLockGeneralControl(false, batch.getUnitName());
                } else {
                    showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error holding the Batch", "Please close the running batch first");
                }
            }, () -> {
                showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error aborting the Batch", "Please start the new batch first");
            });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }
    private void onCloseBatch(MouseEvent action) {
        try {
        controller.getBatchControllerDataForUnit(batch.getUnitName()).ifPresentOrElse(batchControllerData -> {
                batch.setOrder(BatchOrders.Close.name());
                controller.UpdateBatchControlOrder(batch.getId(), BatchOrders.Close.name());
                controller.updateLockGeneralControl(false, batch.getUnitName());
                if ((batch.getState().equals(BatchStates.Idle.name())) || (batch.getState().equals(BatchStates.Finished.name()))) {
                    showPopupWindow(Alert.AlertType.INFORMATION, "Batch info ", "Closing batch ...", "Batch will be transfered to archieve");
                } else {
                    showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error closing the Batch", "Please finish the running batch first or abort it");
                }
        }, () -> {
            showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error aborting the Batch", "Please start the new batch first");
        });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }
    private void onTerminateBatch(MouseEvent action) {
        try {
            controller.getBatchControllerDataForUnit(batch.getUnitName()).ifPresentOrElse(data -> {
                data.setCurrentParallelStepsNo(0);
                data.setRunningBatchID(0L);
                data.setLockGeneralControl(false);
                data.setControlBit(false);
                controller.updateBatchControllerData(data);

                batch.setOrder(BatchOrders.Close.name());
                batch.setState(BatchStates.Idle.name());
                controller.updateBatch(batch);

                showPopupWindow(Alert.AlertType.WARNING, "Warning ", "Terminating batch", "Batch terminated successfully");
                eventOnClose.Action(this);
            }, () -> {
                showPopupWindow(Alert.AlertType.ERROR, "Error ", "Error terminating the Batch", "Please finish the running batch first or abort it");
            });
        } catch (Exception e) {
            e.printStackTrace();
            showErrorWindowForException(e.getMessage(), e);
        }
    }
    private void onModeChange(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        switch (newValue) {
            case "Idle" -> {
                batchState.setText(newValue);
                batchState.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                start.setDisable(false);
                abort.setDisable(false);
                hold.setDisable(true);
                resume.setDisable(false);
                close.setDisable(true);
                terminate.setDisable(false);
            }
            case "Created" -> {
                batchState.setText(newValue);
                batchState.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                start.setDisable(false);
                abort.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(true);
                close.setDisable(true);
                terminate.setDisable(false);
            }
            case "Running" -> {
                batchState.setText(newValue);
                batchState.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                start.setDisable(true);
                abort.setDisable(false);
                hold.setDisable(false);
                resume.setDisable(true);
                close.setDisable(true);
                terminate.setDisable(false);
            }
            case "Held" -> {
                batchState.setText(newValue);
                batchState.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
                start.setDisable(true);
                abort.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(false);
                close.setDisable(true);
                terminate.setDisable(false);
            }
            case "Aborted" -> {
                batchState.setText(newValue);
                batchState.setBackground(new Background(new BackgroundFill(Color.VIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
                start.setDisable(true);
                abort.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(true);
                close.setDisable(true);
                terminate.setDisable(false);
            }
            case "Finished" -> {
                batchState.setText(newValue);
                batchState.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                start.setDisable(true);
                abort.setDisable(true);
                hold.setDisable(true);
                resume.setDisable(true);
                close.setDisable(false);
                terminate.setDisable(false);
            }
        }
    }

    private void LoadRecipeToGraphicsWithoutEdit() {
        try {
            int stepNo = 0;
            int parallelStepNo = 0;
            pane.getChildren().clear();
            for (BatchParallelStepsModel pSM : batch.getModel().getParallelSteps()) {
                ParallelSteps parallelStepTemp = new ParallelSteps();
                pane.getChildren().add(parallelStepTemp);
                stepNo = 0;
                for (BatchStepModel sm : pSM.getSteps()) {
                    BatchStep step = new BatchStep(batch.getId(), parallelStepNo, stepNo, sm, sm.getPhaseName(), mainWindow);
                    parallelStepTemp.getChildren().add(step);
                    stepNo++;
                }
                parallelStepNo++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createPieChart() {
        try {
            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
            batch.getModel().getParallelSteps().stream().flatMap(item -> item.getSteps().stream()).filter(item -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())).forEach(item -> {
                controller.getMaterialByName(item.getMaterialID()).ifPresentOrElse(material -> {
                    chartData.add(new PieChart.Data(material.getName(), item.getValueParametersData().get("Percentage %")));
                }, () -> {
                    chartData.add(new PieChart.Data("Unknown", item.getValueParametersData().get("Percentage %")));
                });
            });
            if (chartData.isEmpty()) {
                piePane.getChildren().add(new Label("Process batch, does not have any components"));
                return;
            }
            PieChart pieChart = new PieChart(chartData);
            pieChart.setAccessibleText("Batch components");
            pieChart.setLegendVisible(true);
            pieChart.setTitle("Batch components");

            piePane.getChildren().add(pieChart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void update() {
        try {
            controller.getBatchByID(batch.getId()).ifPresent(loadedBatch -> {
                updateOnlineBatchFromDatabaseBatch(batch, loadedBatch);
                Platform.runLater(this::updateBatch);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private synchronized void updateBatch() {
        try {
            pane.getChildren().stream().map((parallelStepNode) -> ((ParallelSteps) parallelStepNode)).forEachOrdered((PS) -> {
                PS.getChildren().stream().map((stepNode) -> ((BatchStep) stepNode)).forEachOrdered(BatchStep::run);
            });
            batchMode.setValue(batch.getState());

            if (batch.getState().equals(BatchStates.Created.name())) {
                setStyle("-fx-background-color: DARKGRAY;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (batch.getState().equals(BatchStates.Idle.name())) {
                setStyle("-fx-background-color: GRAY;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (batch.getState().equals(BatchStates.Running.name())) {
                setStyle("-fx-background-color: LIGHTGREEN;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (batch.getState().equals(BatchStates.Held.name())) {
                setStyle("-fx-background-color: YELLOW;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (batch.getState().equals(BatchStates.Aborted.name())) {
                setStyle("-fx-background-color: VIOLET;-fx-border-color: darkblue; -fx-border-width:0.1;");
            } else if (batch.getState().equals(BatchStates.Finished.name())) {
                setStyle("-fx-background-color: DARKGREEN;-fx-border-color: darkblue; -fx-border-width:0.1;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void updateOnlineBatchFromDatabaseBatch(Batch batch, Batch loadedBatch) {
        try {
            if (loadedBatch != null) {
                batch.setState(loadedBatch.getState());
                for (i = 0; i < batch.getModel().getParallelSteps().size(); i++) {
                    for (j = 0; j < batch.getModel().getParallelSteps().get(i).getSteps().size(); j++) {
                        batch.getModel().getParallelSteps().get(i).getSteps().get(j).setState(loadedBatch.getModel().getParallelSteps().get(i).getSteps().get(j).getState());

                        Map<String, Boolean> checkActualData = batch.getModel().getParallelSteps().get(i).getSteps().get(j).getActualCheckParametersData();
                        Map<String, Double> valueActualData = batch.getModel().getParallelSteps().get(i).getSteps().get(j).getActualvalueParametersData();

                        checkActualData.forEach((paraName, paraValue) -> {
                            boolean value = loadedBatch.getModel().getParallelSteps().get(i).getSteps().get(j).getActualCheckParametersData().get(paraName);
                            checkActualData.replace(paraName, value);
                        });
                        valueActualData.forEach((paraName, paraValue) -> {
                            double value = loadedBatch.getModel().getParallelSteps().get(i).getSteps().get(j).getActualvalueParametersData().get(paraName);
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
        return batch.getId();
    }

    private void showPopupWindow(Alert.AlertType type, String title, String header, String content) {
        Alert Error = new Alert(type);
        Error.setTitle(title);
        Error.setHeaderText(header);
        Error.setContentText(content);
        Error.initOwner(mainWindow);
        Error.initStyle(StageStyle.UTILITY);
        Error.show();
    }
    private void showErrorWindowForException(String header, Throwable e) {
        Platform.runLater(() -> {
            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
            exceptionDialog.setHeaderText(header);
            exceptionDialog.getDialogPane().setMaxWidth(500);
            exceptionDialog.initOwner(mainWindow);
            exceptionDialog.initModality(Modality.WINDOW_MODAL);
            exceptionDialog.initStyle(StageStyle.UTILITY);
            exceptionDialog.show();
        });
    }
}
