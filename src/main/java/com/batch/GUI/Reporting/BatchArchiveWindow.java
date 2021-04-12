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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private TableView<Batch> table = new TableView<>();
    private TableColumn<Batch, Long> NameColumn = new TableColumn<>("ID");
    private TableColumn<Batch, String> UnitNameColumn = new TableColumn<>("Unit name");
    private TableColumn<Batch, String> BatchNameColumn = new TableColumn<>("Batch name");
    private TableColumn<Batch, String> CreationDateColumn = new TableColumn<>("Creation date");
    private TableColumn<Batch, String> CreationTimeColumn = new TableColumn<>("Creation time");
    private TableColumn<Batch, String> CommentColumn = new TableColumn<>("Comment");

    private double totalLoaded, totalRequired, totalError;
    private int counter = 1;

    private final ReportsController controller;
    private final ReportsModel model;

    private BatchArchiveWindow(Stage Window) {
        mainWindow = Window;
        this.controller = ApplicationContext.applicationContext.getBean(ReportsController.class);
        this.model = controller.getModel();
        graphicsBuilder();
        actionHandling();
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
        fromPicker.setPrefWidth(300);
        toPicker.setPrefWidth(300);
        batchNameField.setPrefWidth(820);
        fromLabel.setPrefWidth(200);
        toLabel.setPrefWidth(200);
        batchNameLabel.setPrefWidth(200);

        filterByDate.setPrefWidth(250);
        filterByName.setPrefWidth(250);

        dataEntry.add(fromLabel, 1, 1);
        dataEntry.add(fromPicker, 2, 1);
        dataEntry.add(toLabel, 3, 1);
        dataEntry.add(toPicker, 4, 1);
        dataEntry.add(batchNameLabel, 1, 2);
        dataEntry.add(batchNameField, 2, 2, 3, 1);
        dataEntry.setPadding(new Insets(10));
        dataEntry.setVgap(10);
        dataEntry.setHgap(10);

        bar.getItems().addAll(filterByDate, new Separator(), filterByName);
        //table configuration
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        UnitNameColumn.setCellValueFactory(new PropertyValueFactory<>("unitName"));
        BatchNameColumn.setCellValueFactory(new PropertyValueFactory<>("batchName"));
        CreationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        CreationTimeColumn.setCellValueFactory(new PropertyValueFactory<>("creationTime"));
        CommentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        table.getColumns().addAll(NameColumn, UnitNameColumn, BatchNameColumn, CreationDateColumn, CreationTimeColumn, CommentColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(model.getList());
        table.prefHeightProperty().bind(root.heightProperty());

        root.getChildren().addAll(dataEntry, bar, table);
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        model.getFromDate().bind(fromPicker.valueProperty());
        model.getToDate().bind(toPicker.valueProperty());
        model.getFilterString().bind(batchNameField.textProperty());

        setTitle("Reporting manager");
        initOwner(mainWindow);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        setScene(new Scene(root, 800, 800));
        setMinHeight(500);
    }
    private void actionHandling() {

        table.setOnMousePressed(action -> {
            if (action.getButton().equals(MouseButton.PRIMARY) && action.getClickCount() == 2 && table.getItems().size() > 0 && !table.getSelectionModel().isEmpty()) {

                try {
                    Batch batch = table.getSelectionModel().getSelectedItem();
                    String batchName = batch.getBatchName();
                    long ID = batch.getId();
                    LocalDate date = batch.getCreationDate();
                    LocalTime time = batch.getCreationTime();

                    List<ReportTableDataModel> data = batch.getModel().getParallelSteps()
                            .stream().flatMap(item -> item.getSteps().stream())
                            .filter(item -> !item.getPhaseName().equals("Start"))
                            .filter(item -> !item.getPhaseName().equals("End"))
                            .filter(item -> item.getPhaseType().equals(PhasesTypes.Dose_phase.name().replace("_", " ").trim()))
                            .map(item -> {
                                double required = item.getValueParametersData().get("Percentage %");
                                double loaded = item.getActualvalueParametersData().get("Percentage %");
                                double error = loaded - required;

                                required = Round.RoundDouble(required, 4);
                                loaded = Round.RoundDouble(loaded, 4);
                                error = Round.RoundDouble(error, 4);

                                String materialName = controller.getMaterialById(item.getMaterialID()).map(Material::getName).orElse("");

                                return new ReportTableDataModel(0, materialName, required, loaded, error, 0.0, 0.0);
                            })
                            .collect(Collectors.toList());

                    totalRequired = data.stream().map(ReportTableDataModel::getRequired).reduce(0.0, Double::sum);
                    totalLoaded = data.stream().map(ReportTableDataModel::getLoaded).reduce(0.0, Double::sum);
                    totalError = data.stream().map(ReportTableDataModel::getError).reduce(0.0, Double::sum);
                    counter = 1;
                    data = data
                            .stream()
                            .map(item -> new ReportTableDataModel(counter++, item.getMaterialName(), item.getRequired(), item.getLoaded(), item.getError(), Round.RoundDouble((item.getRequired() / totalRequired * 100), 4), Round.RoundDouble((item.getLoaded() / totalLoaded * 100), 4)))
                            .collect(Collectors.toList());
                    double totalActualPercent = data.stream().map(ReportTableDataModel::getActualPercent).reduce(0.0, Double::sum);
                    data.add(new ReportTableDataModel(counter, "", Round.RoundDouble(totalRequired, 4), Round.RoundDouble(totalLoaded, 4), Round.RoundDouble(totalError, 4), 100.0, totalActualPercent));
                    BatchReport report = new BatchReport(new ReportModel(ID, batchName, date, time, data), this);
                    report.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert Error = new Alert(Alert.AlertType.ERROR);
                    Error.setTitle("Error ");
                    Error.setHeaderText("Error Importing data");
                    Error.setContentText("Please select Table row again");
                    Error.initOwner(this);
                    Error.showAndWait();
                }
            }
        });

        filterByDate.setOnMouseClicked(controller::onFilterByDate);
        filterByName.setOnMouseClicked(controller::onFilterByName);
        showingProperty().addListener((observable, oldValue, newValue) -> {if (newValue){ controller.updateTable();}});
    }

    @Override
    public void close() {
        hide();
    }

}
