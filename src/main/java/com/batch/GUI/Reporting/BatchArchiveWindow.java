
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BatchArchiveWindow extends Stage {
    private static volatile BatchArchiveWindow Singleton = null;
    private DataEntryPartition dataEntry = new DataEntryPartition("Enter filters");
    private VBox root = new VBox();
    private ToolBar bar = new ToolBar();
    private DatePicker fromPicker = new DatePicker();
    private DatePicker toPicker = new DatePicker();
    private TextField batchNameField = new TextField();
    private Label fromLabel = new Label("Filter by date from");
    private Label toLabel = new Label("Filter by date to");
    private Label batchNameLabel = new Label("Filter by batch name");
    private Button filterByDate = new Button("Filter by date");
    private Button filterByName = new Button("Filter by name");
    private Stage mainWindow = null;
    private TableView<Batch> table = new TableView();
    private TableColumn<Batch, Long> NameColumn = new TableColumn("ID");
    private TableColumn<Batch, String> UnitNameColumn = new TableColumn("Unit name");
    private TableColumn<Batch, String> BatchNameColumn = new TableColumn("Batch name");
    private TableColumn<Batch, String> CreationDateColumn = new TableColumn("Creation date");
    private TableColumn<Batch, String> CreationTimeColumn = new TableColumn("Creation time");
    private TableColumn<Batch, String> CommentColumn = new TableColumn("Comment");
    private final ObjectProperty<Cursor> CURSOR_DEFAULT;
    private final ObjectProperty<Cursor> CURSOR_WAIT;
    private double totalLoaded;
    private double totalRequired;
    private double totalError;
    private int counter;
    private final ReportsController controller;
    private final ReportsModel model;

    private BatchArchiveWindow(Stage Window) {
        this.CURSOR_DEFAULT = new SimpleObjectProperty(Cursor.DEFAULT);
        this.CURSOR_WAIT = new SimpleObjectProperty(Cursor.WAIT);
        this.counter = 1;
        this.mainWindow = Window;
        this.controller = (ReportsController)ApplicationContext.applicationContext.getBean(ReportsController.class);
        this.model = this.controller.getModel();
        this.graphicsBuilder();
        this.actionHandling();
    }

    public static BatchArchiveWindow getWindow(Stage Window) {
        synchronized(BatchArchiveWindow.class) {
            if (Singleton == null) {
                Singleton = new BatchArchiveWindow(Window);
            }
        }

        return Singleton;
    }

    private void graphicsBuilder() {
        this.fromPicker.setPrefWidth((double)300.0F);
        this.toPicker.setPrefWidth((double)300.0F);
        this.batchNameField.setPrefWidth((double)820.0F);
        this.fromLabel.setPrefWidth((double)200.0F);
        this.toLabel.setPrefWidth((double)200.0F);
        this.batchNameLabel.setPrefWidth((double)200.0F);
        this.filterByDate.setPrefWidth((double)250.0F);
        this.filterByName.setPrefWidth((double)250.0F);
        this.dataEntry.add(this.fromLabel, 1, 1);
        this.dataEntry.add(this.fromPicker, 2, 1);
        this.dataEntry.add(this.toLabel, 3, 1);
        this.dataEntry.add(this.toPicker, 4, 1);
        this.dataEntry.add(this.batchNameLabel, 1, 2);
        this.dataEntry.add(this.batchNameField, 2, 2, 3, 1);
        this.dataEntry.setPadding(new Insets((double)10.0F));
        this.dataEntry.setVgap((double)10.0F);
        this.dataEntry.setHgap((double)10.0F);
        this.bar.getItems().addAll(new Node[]{this.filterByDate, new Separator(), this.filterByName});
        this.NameColumn.setCellValueFactory(new PropertyValueFactory("id"));
        this.UnitNameColumn.setCellValueFactory(new PropertyValueFactory("unitName"));
        this.BatchNameColumn.setCellValueFactory(new PropertyValueFactory("batchName"));
        this.CreationDateColumn.setCellValueFactory(new PropertyValueFactory("creationDate"));
        this.CreationTimeColumn.setCellValueFactory(new PropertyValueFactory("creationTime"));
        this.CommentColumn.setCellValueFactory(new PropertyValueFactory("comment"));
        this.table.getColumns().addAll(new TableColumn[]{this.NameColumn, this.UnitNameColumn, this.BatchNameColumn, this.CreationDateColumn, this.CreationTimeColumn, this.CommentColumn});
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.table.setItems(this.model.getList());
        this.table.prefHeightProperty().bind(this.root.heightProperty());
        this.root.getChildren().addAll(new Node[]{this.dataEntry, this.bar, this.table});
        this.root.setPadding(new Insets((double)10.0F));
        this.root.setSpacing((double)10.0F);
        this.model.getFromDate().bind(this.fromPicker.valueProperty());
        this.model.getToDate().bind(this.toPicker.valueProperty());
        this.model.getFilterString().bind(this.batchNameField.textProperty());
        this.setTitle("Reporting manager");
        this.initOwner(this.mainWindow);
        this.initModality(Modality.WINDOW_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setScene(new Scene(this.root, (double)800.0F, (double)800.0F));
        this.setMinHeight((double)500.0F);
    }

    private void actionHandling() {
        this.table.setOnMousePressed((action) -> {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2 && this.table.getItems().size() > 0 && !this.table.getSelectionModel().isEmpty()) {
                try {
                    Batch batch = (Batch)this.table.getSelectionModel().getSelectedItem();
                    String batchName = batch.getBatchName();
                    long ID = batch.getId();
                    String client = batch.getClient();
                    String product = batch.getProduct();
                    String comment = batch.getComment();
                    LocalDate date = batch.getCreationDate();
                    LocalTime time = batch.getCreationTime();
                    LocalDateTime endTime = batch.getEndTime();
                    List<ReportTableDataModel> data = (List)batch.getModel().getParallelSteps().stream().flatMap((item) -> item.getSteps().stream()).filter((item) -> !item.getPhaseName().equals("Start")).filter((item) -> !item.getPhaseName().equals("End")).filter((item) -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim())).map((item) -> {
                        try {
                            double required = (Double)item.getValueParametersData().get("Percentage %");
                            double loaded = (double)0.0F;

                            try {
                                loaded = (Double)item.getActualvalueParametersData().get("Percentage %");
                            } catch (Exception var9) {
                            }

                            double error = loaded - required;
                            required = Round.RoundDouble(required, 4);
                            loaded = Round.RoundDouble(loaded, 4);
                            error = Round.RoundDouble(error, 4);
                            String materialName = (String)this.controller.getMaterialById(item.getMaterialID()).map(Material::getName).orElse("");
                            return new ReportTableDataModel(0, materialName, required, loaded, error, (double)0.0F, (double)0.0F);
                        } catch (Exception var10) {
                            return new ReportTableDataModel(0, "MaterialName", (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
                        }
                    }).collect(Collectors.toList());
                    this.totalRequired = (Double)data.stream().map(ReportTableDataModel::getRequired).reduce((double)0.0F, Double::sum);
                    this.totalLoaded = (Double)data.stream().map(ReportTableDataModel::getLoaded).reduce((double)0.0F, Double::sum);
                    this.totalError = (Double)data.stream().map(ReportTableDataModel::getError).reduce((double)0.0F, Double::sum);
                    this.counter = 1;
                    data = (List)data.stream().map((item) -> new ReportTableDataModel(this.counter++, item.getMaterialName(), item.getRequired(), item.getLoaded(), item.getError(), Round.RoundDouble(item.getRequired() / this.totalRequired * (double)100.0F, 4), Round.RoundDouble(item.getLoaded() / this.totalLoaded * (double)100.0F, 4))).collect(Collectors.toList());
                    double totalActualPercent = (Double)data.stream().map(ReportTableDataModel::getActualPercent).reduce((double)0.0F, Double::sum);
                    data.add(new ReportTableDataModel(this.counter, "", Round.RoundDouble(this.totalRequired, 4), Round.RoundDouble(this.totalLoaded, 4), Round.RoundDouble(this.totalError, 4), (double)100.0F, totalActualPercent));
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
