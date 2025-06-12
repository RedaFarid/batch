package com.batch.GUI.Reporting;

import com.batch.ApplicationContext;
import com.batch.DTO.RecipeSystemDataDefinitions.PhasesTypes;
import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.GUI.Controls.DataEntryPartition;
import com.batch.GUI.Reporting.Reports.BatchReport;
import com.batch.GUI.Reporting.Reports.ReportModel;
import com.batch.GUI.Reporting.Reports.ReportTableDataModel;
import com.batch.Utilities.Round;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BatchArchiveWindow extends Stage {
    private static volatile BatchArchiveWindow Singleton = null;
    private final ObjectProperty<Cursor> CURSOR_DEFAULT;
    private final ObjectProperty<Cursor> CURSOR_WAIT;
    private final ReportsController controller;
    private final ReportsModel model;
    private final DataEntryPartition dataEntry = new DataEntryPartition("Enter filters");
    private final VBox root = new VBox();
    private final ToolBar bar = new ToolBar();
    private final DatePicker fromPicker = new DatePicker();
    private final DatePicker toPicker = new DatePicker();
    private final TextField batchNameField = new TextField();
    private final Label fromLabel = new Label("Filter by date from");
    private final Label toLabel = new Label("Filter by date to");
    private final Label batchNameLabel = new Label("Filter by batch name");
    private final Button filterByDate = new Button("Filter by date");
    private final Button filterByName = new Button("Filter by name");
    private Stage mainWindow = null;
    private final TableView<Batch> table = new TableView();
    private final TableColumn<Batch, Long> NameColumn = new TableColumn("ID");
    private final TableColumn<Batch, String> UnitNameColumn = new TableColumn("Unit name");
    private final TableColumn<Batch, String> BatchNameColumn = new TableColumn("Batch name");
    private final TableColumn<Batch, String> CreationDateColumn = new TableColumn("Creation date");
    private final TableColumn<Batch, String> CreationTimeColumn = new TableColumn("Creation time");
    private final TableColumn<Batch, String> CommentColumn = new TableColumn("Comment");
    private double totalLoaded;
    private double totalRequired;
    private double totalError;
    private int counter;

    private BatchArchiveWindow(Stage Window) {
        this.CURSOR_DEFAULT = new SimpleObjectProperty(Cursor.DEFAULT);
        this.CURSOR_WAIT = new SimpleObjectProperty(Cursor.WAIT);
        this.counter = 1;
        this.mainWindow = Window;
        this.controller = ApplicationContext.applicationContext.getBean(ReportsController.class);
        this.model = this.controller.getModel();
        this.graphicsBuilder();
        this.actionHandling();
    }

    public static BatchArchiveWindow getWindow(Stage Window) {
        synchronized (BatchArchiveWindow.class) {
            if (Singleton == null) {
                Singleton = new BatchArchiveWindow(Window);
            }
        }

        return Singleton;
    }

    private void graphicsBuilder() {
        this.fromPicker.setPrefWidth(300.0F);
        this.toPicker.setPrefWidth(300.0F);
        this.batchNameField.setPrefWidth(820.0F);
        this.fromLabel.setPrefWidth(200.0F);
        this.toLabel.setPrefWidth(200.0F);
        this.batchNameLabel.setPrefWidth(200.0F);
        this.filterByDate.setPrefWidth(250.0F);
        this.filterByName.setPrefWidth(250.0F);
        this.dataEntry.add(this.fromLabel, 1, 1);
        this.dataEntry.add(this.fromPicker, 2, 1);
        this.dataEntry.add(this.toLabel, 3, 1);
        this.dataEntry.add(this.toPicker, 4, 1);
        this.dataEntry.add(this.batchNameLabel, 1, 2);
        this.dataEntry.add(this.batchNameField, 2, 2, 3, 1);
        this.dataEntry.setPadding(new Insets(10.0F));
        this.dataEntry.setVgap(10.0F);
        this.dataEntry.setHgap(10.0F);
        this.bar.getItems().addAll(this.filterByDate, new Separator(), this.filterByName);
        this.NameColumn.setCellValueFactory(new PropertyValueFactory("id"));
        this.UnitNameColumn.setCellValueFactory(new PropertyValueFactory("unitName"));
        this.BatchNameColumn.setCellValueFactory(new PropertyValueFactory("batchName"));
        this.CreationDateColumn.setCellValueFactory(new PropertyValueFactory("creationDate"));
        this.CreationTimeColumn.setCellValueFactory(new PropertyValueFactory("creationTime"));
        this.CommentColumn.setCellValueFactory(new PropertyValueFactory("comment"));
        this.table.getColumns().addAll(this.NameColumn, this.UnitNameColumn, this.BatchNameColumn, this.CreationDateColumn, this.CreationTimeColumn, this.CommentColumn);
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.table.setItems(this.model.getList());
        this.table.prefHeightProperty().bind(this.root.heightProperty());
        this.root.getChildren().addAll(this.dataEntry, this.bar, this.table);
        this.root.setPadding(new Insets(10.0F));
        this.root.setSpacing(10.0F);
        this.model.getFromDate().bind(this.fromPicker.valueProperty());
        this.model.getToDate().bind(this.toPicker.valueProperty());
        this.model.getFilterString().bind(this.batchNameField.textProperty());
        this.setTitle("Reporting manager");
        this.initOwner(this.mainWindow);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setScene(new Scene(this.root, 800.0F, 800.0F));
        this.setMinHeight(500.0F);
    }

    private void actionHandling() {
        this.table.setOnMousePressed((action) -> {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2 && this.table.getItems().size() > 0 && !this.table.getSelectionModel().isEmpty()) {
                try {
                    Batch batch = this.table.getSelectionModel().getSelectedItem();
                    String batchName = batch.getBatchName();
                    long ID = batch.getId();
                    String client = batch.getClient();
                    String product = batch.getProduct();
                    String comment = batch.getComment();
                    LocalDate date = batch.getCreationDate();
                    LocalTime time = batch.getCreationTime();
                    LocalDateTime endTime = batch.getEndTime();
                    List<ReportTableDataModel> data = batch.getModel().getParallelSteps().stream().flatMap((item) -> item.getSteps().stream()).filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).filter((item) -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())).map((item) -> {
                        try {
                            double required = item.getValueParametersData().get("Percentage %");
                            double loaded = 0.0F;

                            try {
                                loaded = item.getActualvalueParametersData().get("Percentage %");
                            } catch (Exception var9) {
                            }

                            double error = loaded - required;
                            required = Round.RoundDouble(required, 4);
                            loaded = Round.RoundDouble(loaded, 4);
                            error = Round.RoundDouble(error, 4);
                            String materialName = this.controller.getMaterialById(item.getMaterialID()).map(Material::getName).orElse("");
                            return new ReportTableDataModel(0, materialName, required, loaded, error, 0.0F, 0.0F);
                        } catch (Exception var10) {
                            return new ReportTableDataModel(0, "MaterialName", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                        }
                    }).collect(Collectors.toList());
                    this.totalRequired = data.stream().map(ReportTableDataModel::getRequired).reduce((double) 0.0F, Double::sum);
                    this.totalLoaded = data.stream().map(ReportTableDataModel::getLoaded).reduce((double) 0.0F, Double::sum);
                    this.totalError = data.stream().map(ReportTableDataModel::getError).reduce((double) 0.0F, Double::sum);
                    this.counter = 1;
                    data = data.stream().map((item) -> new ReportTableDataModel(this.counter++, item.getMaterialName(), item.getRequired(), item.getLoaded(), item.getError(), Round.RoundDouble(item.getRequired() / this.totalRequired * (double) 100.0F, 4), Round.RoundDouble(item.getLoaded() / this.totalLoaded * (double) 100.0F, 4))).collect(Collectors.toList());
                    double totalActualPercent = data.stream().map(ReportTableDataModel::getActualPercent).reduce((double) 0.0F, Double::sum);
                    data.add(new ReportTableDataModel(this.counter, "", Round.RoundDouble(this.totalRequired, 4), Round.RoundDouble(this.totalLoaded, 4), Round.RoundDouble(this.totalError, 4), 100.0F, totalActualPercent));
                    ReportModel var10002 = new ReportModel(ID, batchName, date, time, endTime, product, client, comment, data);
                    ReportsController var10004 = this.controller;
                    Objects.requireNonNull(var10004);
                    BatchReport report = new BatchReport(var10002, this, var10004::exportReport);
                    report.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert Error = new Alert(AlertType.ERROR);
                    Error.setTitle("Error ");
                    Error.setHeaderText("Error Importing data");
                    Error.setContentText("Please select Table row again");
                    Error.initOwner(this);
                    Error.showAndWait();
                }
            }

        });
        this.filterByDate.setOnMouseClicked((event) -> {
            ReadOnlyBooleanProperty readOnlyBooleanProperty = this.controller.onFilterByDate();
            this.root.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
        });
        this.filterByName.setOnMouseClicked((event) -> {
            ReadOnlyBooleanProperty readOnlyBooleanProperty = this.controller.onFilterByName();
            this.root.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
        });
        this.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ReadOnlyBooleanProperty readOnlyBooleanProperty = this.controller.updateTable();
                this.root.cursorProperty().bind(Bindings.when(readOnlyBooleanProperty).then(this.CURSOR_WAIT).otherwise(this.CURSOR_DEFAULT));
            }

        });
    }

    public void close() {
        this.hide();
    }
}
