package com.batch.GUI.Reporting.MaterialConsumption;

import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.GUI.Controls.DataEntryPartition;
import com.batch.GUI.Reporting.Reports.BatchReport;
import com.batch.GUI.Reporting.Reports.ReportModel;
import com.batch.GUI.Reporting.Reports.ReportTableDataModel;
import com.batch.GUI.Reporting.ReportsController;
import com.batch.Utilities.Round;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MaterialConsumptionWindow extends Tab {
    private static volatile MaterialConsumptionWindow Singleton = null;
    private final ObjectProperty<Cursor> CURSOR_DEFAULT;
    private final ObjectProperty<Cursor> CURSOR_WAIT;
    private final  MaterialConsumptionController controller;
    private final MaterialConsumptionModel model;
    private final DataEntryPartition dataEntry = new DataEntryPartition("Enter filters");
    private final VBox root = new VBox();
    private final ToolBar bar = new ToolBar();
    private final DatePicker fromPicker = new DatePicker();
    private final DatePicker toPicker = new DatePicker();
    private final Label fromLabel = new Label("Filter by date from");
    private final Label toLabel = new Label("Filter by date to");
    private final Button filterByDate = new Button("Filter by date");
    private final Button filterByName = new Button("Filter by name");
    private final Button exportExcel = new Button("Export Excel");
    private final Button showReport = new Button("Show Report");
    private final TableView<MaterialConsumptionModel.Item> table = new TableView();
    private final TableColumn<MaterialConsumptionModel.Item, String> materialNameColumn = new TableColumn("Material name");
    private final TableColumn<MaterialConsumptionModel.Item, Double> requiredColumn = new TableColumn("Required");
    private final TableColumn<MaterialConsumptionModel.Item, Double> actualColumn = new TableColumn("Loaded");
    private final TableColumn<MaterialConsumptionModel.Item, Double> errorColumn = new TableColumn("Error");

    private Stage mainWindow = null;
    private double totalLoaded;
    private double totalRequired;
    private double totalError;
    private int counter;
    private ReportModel reportModel;

    public MaterialConsumptionWindow(Stage Window) {
        this.CURSOR_DEFAULT = new SimpleObjectProperty(Cursor.DEFAULT);
        this.CURSOR_WAIT = new SimpleObjectProperty(Cursor.WAIT);
        this.counter = 1;
        this.mainWindow = Window;
        this.controller = ApplicationContext.applicationContext.getBean(MaterialConsumptionController.class);
        this.model = this.controller.getModel();
        reportModel = new ReportModel();
        this.graphicsBuilder();
        this.actionHandling();
    }

    public static MaterialConsumptionWindow getWindow(Stage Window) {
        synchronized (MaterialConsumptionWindow.class) {
            if (Singleton == null) {
                Singleton = new MaterialConsumptionWindow(Window);
            }
        }

        return Singleton;
    }

    private void graphicsBuilder() {
        this.fromPicker.setPrefWidth(300.0F);
        this.toPicker.setPrefWidth(300.0F);
        this.fromLabel.setPrefWidth(200.0F);
        this.toLabel.setPrefWidth(200.0F);
        this.filterByDate.setPrefWidth(250.0F);
        this.filterByName.setPrefWidth(250.0F);
        this.exportExcel.setPrefWidth(250.0F);
        this.showReport.setPrefWidth(250.0F);
        this.dataEntry.add(this.fromLabel, 1, 1);
        this.dataEntry.add(this.fromPicker, 2, 1);
        this.dataEntry.add(this.toLabel, 3, 1);
        this.dataEntry.add(this.toPicker, 4, 1);
        this.dataEntry.setPadding(new Insets(10.0F));
        this.dataEntry.setVgap(10.0F);
        this.dataEntry.setHgap(10.0F);
        this.bar.getItems().addAll(this.filterByDate, new Separator(),this.exportExcel,this.showReport);
        this.materialNameColumn.setCellValueFactory(new PropertyValueFactory("materialName"));
        this.requiredColumn.setCellValueFactory(new PropertyValueFactory("required"));
        this.actualColumn.setCellValueFactory(new PropertyValueFactory("actual"));
        this.errorColumn.setCellValueFactory(new PropertyValueFactory("error"));
        this.table.getColumns().addAll( this.materialNameColumn, this.requiredColumn,this.actualColumn, this.errorColumn);
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.table.setItems(this.model.getList());
        this.table.prefHeightProperty().bind(this.root.heightProperty());
        this.root.getChildren().addAll(this.dataEntry, this.bar, this.table);
        this.root.setPadding(new Insets(10.0F));
        this.root.setSpacing(10.0F);
        root.setMinSize(1250,800);
        this.model.getFromDate().bind(this.fromPicker.valueProperty());
        this.model.getToDate().bind(this.toPicker.valueProperty());
//        this.setTitle("Reporting manager");
//        this.initOwner(this.mainWindow);
//        this.initModality(Modality.WINDOW_MODAL);
//        this.initStyle(StageStyle.UTILITY);
//
//        this.setScene(new Scene(this.root, 1250.0F, 800.0F));
//        this.setMinHeight(500.0F);
        this.setContent(root);
    }

    private void actionHandling() {
        this.filterByDate.setOnMouseClicked((event) -> {
            ReadOnlyBooleanProperty readOnlyBooleanProperty = this.controller.onFilterByDate();
            this.root.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
        });
        this.exportExcel.setOnMouseClicked((event) -> {
          onExportExcel();
        });
        this.showReport.setOnAction(e -> {
//            Batch batch = this.table.getSelectionModel().getSelectedItem();
//            String batchName = batch.getBatchName();
//            long ID = batch.getId();
//            String client = batch.getClient();
//            String product = batch.getProduct();
//            String comment = batch.getComment();
//            LocalDate date = batch.getCreationDate();
//            LocalTime time = batch.getCreationTime();
//            LocalDateTime endTime = batch.getEndTime();
//            String  createdBy = batch.getCreatedBy();
//            List<ReportTableDataModel> data = batch.getModel().getParallelSteps().stream().flatMap((item) -> item.getSteps().stream()).filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).filter((item) -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())).map((item) -> {
//                try {
//                    double required = item.getValueParametersData().get("Percentage %");
//                    double loaded = 0.0F;
//
//                    try {
//                        loaded = item.getActualvalueParametersData().get("Percentage %");
//                    } catch (Exception var9) {
//                    }
//
//                    double error = loaded - required;
//                    required = Round.RoundDouble(required, 4);
//                    loaded = Round.RoundDouble(loaded, 4);
//                    error = Round.RoundDouble(error, 4);
//                    String materialName = this.controller.getMaterialById(item.getMaterialID()).map(Material::getName).orElse("");
//                    return new ReportTableDataModel(0, materialName, required, loaded, error, 0.0F, 0.0F);
//                } catch (Exception var10) {
//                    return new ReportTableDataModel(0, "MaterialName", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
//                }
//            }).collect(Collectors.toList());
//            this.counter = 1;
//            data = data.stream().map((item) -> new ReportTableDataModel(this.counter++, item.getMaterialName(), item.getRequired(), item.getLoaded(), item.getError(), Round.RoundDouble(item.getRequired() / this.totalRequired * (double) 100.0F, 4), Round.RoundDouble(item.getLoaded() / this.totalLoaded * (double) 100.0F, 4))).collect(Collectors.toList());
//            double totalActualPercent = data.stream().map(ReportTableDataModel::getActualPercent).reduce((double) 0.0F, Double::sum);
//            data.add(new ReportTableDataModel(this.counter, "", Round.RoundDouble(this.totalRequired, 4), Round.RoundDouble(this.totalLoaded, 4), Round.RoundDouble(this.totalError, 4), 100.0F, totalActualPercent));
//            ReportModel var10002 = new ReportModel(ID, batchName, date, time,createdBy, endTime, product, client, comment, data);
            controller.onReport(reportModel).whenComplete((pane, throwable) -> {
                if (throwable != null) {
                    return;
                }
                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setScene(new Scene(pane));
                    stage.setTitle("Batch Report");
                    stage.initOwner(this.mainWindow);
                    stage.setWidth(1300);
                    stage.show();
                });
            });
        });
//        this.showingProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                ReadOnlyBooleanProperty readOnlyBooleanProperty = this.controller.updateTable();
//                this.root.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
//            }
//
//        });
    }

    private void onExportExcel() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export to excel");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel format", "*.xlsx"));
        ObservableList<MaterialConsumptionModel.Item> list = this.table.getItems();
        chooser.setInitialFileName(model.getFromDate().getValue()+"  to "+model.getToDate().getValue());
        File file = chooser.showSaveDialog(this.mainWindow);
        controller.onExportExcel(file,list);
    }

}
