package com.batch.GUI.Reporting.MaterialConsumption;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Services.BatchesService;
import com.batch.GUI.Reporting.Reports.ReportModel;
import com.batch.GUI.Reporting.Reports.ReportTableDataModel;
import com.batch.GUI.Reporting.ReportsModel;
import com.batch.Reporting.ReportsDTO.Batches;
import com.batch.Reporting.ReportsDTO.DTO;
import com.batch.Reporting.ReportsDetails.ReportDetailsFactory;
import com.batch.Utilities.Round;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class MaterialConsumptionController {
    private final MaterialsRepository materialsRepository;
    private final BatchesService batchesService;
    private final TaskExecutor executor;
    private final MaterialConsumptionModel model = new MaterialConsumptionModel();
    private int counter;
    @Autowired
    private ReportDetailsFactory reportDetailsFactory;

    public MaterialConsumptionController(final MaterialsRepository materialsRepository, final BatchesService batchesService, final TaskExecutor executor) {
        this.materialsRepository = materialsRepository;
        this.batchesService = batchesService;
        this.executor = executor;
    }

    public MaterialConsumptionModel getModel() {
        return this.model;
    }

    public Optional<Material> getMaterialById(long materialID) {
        return this.materialsRepository.findById(materialID);
    }

    private ReadOnlyBooleanProperty update(Function<List<MaterialConsumptionModel.Item>, List<MaterialConsumptionModel.Item>> function) {
        Task<Boolean> updateTask = this.updateTask(function);
        this.executor.execute(updateTask);
        return updateTask.runningProperty();
    }

    private List<MaterialConsumptionModel.Item> getMaterialsConsumption() {
        List<MaterialConsumptionModel.Item> list = FXCollections.observableArrayList();
        if (model.getFromDate().getValue() != null && model.getToDate().getValue() != null) {
            List<ReportTableDataModel> data = batchesService.findAll().stream()
                    .filter((item) -> item.getCreationDate().isAfter(model.getFromDate().getValue()))
                    .filter((item) -> item.getCreationDate().isBefore(model.getToDate().get()))
                    .flatMap(batch -> batch.getModel().getParallelSteps().parallelStream()
                            .flatMap((item) -> item.getSteps().stream().map((step) -> {
                                try {
                                    double required = step.getValueParametersData().get("Percentage %");
                                    double loaded = 0.0F;

                                    try {
                                        loaded = step.getActualvalueParametersData().get("Percentage %");
                                    } catch (Exception var9) {
                                    }

                                    double error = loaded - required;
                                    required = Round.RoundDouble(required, 4);
                                    loaded = Round.RoundDouble(loaded, 4);
                                    error = Round.RoundDouble(error, 4);
                                    String materialName = getMaterialById(step.getMaterialID()).map(Material::getName).orElse("");
                                    return new ReportTableDataModel(0, materialName, required, loaded, error, 0.0F, 0.0F);
                                } catch (Exception var10) {
                                    return new ReportTableDataModel(0, "MaterialName", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                                }
                            }))).collect(Collectors.toList());
            counter = 1;
            System.out.println("FFF"+data);
            data = data.stream().map((item) -> new ReportTableDataModel(counter++, item.getMaterialName(), item.getRequired(), item.getLoaded(), item.getError(), 0.0, 0.0)).collect(Collectors.toList());
            Map<String, MaterialConsumptionModel.Item> map = new HashMap<>();

            data.stream().forEach(item -> {
                String materialName = item.getMaterialName();
                double required = item.getRequired();
                double act = item.getLoaded();
                double error = item.getError();
                if (map.containsKey(materialName)) {
                    Double act1 = map.get(materialName).getActual();
                    Double error1 = map.get(materialName).getError();
                    Double required1 = map.get(materialName).getRequired();
                    map.get(materialName).setActual(act + act1);
                    map.get(materialName).setError(error + error1);
                    map.get(materialName).setRequired(required + required1);

                } else {
                    MaterialConsumptionModel.Item item1 = new MaterialConsumptionModel.Item(materialName, required, act, error);
                    map.put(materialName, item1);
                }
            });
        }
        System.err.println("jjj --   "+list);
        return list;
    }

    private Task<Boolean> updateTask(final Function<List<MaterialConsumptionModel.Item>, List<MaterialConsumptionModel.Item>> function) {
        ObservableList<MaterialConsumptionModel.Item> dataList = model.getList();
        return new Task<Boolean>() {
            protected Boolean call() throws Exception {

                List<MaterialConsumptionModel.Item> dataBaseList = function.apply(getMaterialsConsumption());
                Platform.runLater(() -> dataList.removeAll((Collection) ((ObservableList) dataBaseList.stream().filter((item) -> !dataList.contains(item)).collect(() -> dataList, List::add, List::addAll)).stream().filter((tableListItem) -> dataBaseList.stream().noneMatch((dataBaseItem) -> dataBaseItem.equals(tableListItem))).collect(Collectors.toList())));
                return true;
            }

        };
    }

    public ReadOnlyBooleanProperty onFilterByDate() {
        if (model.getFromDate().getValue() != null && model.getToDate().getValue() != null) {
            this.model.getList().clear();
            return this.update(list ->getMaterialsConsumption());
        } else {
            return new SimpleBooleanProperty(false);
        }
    }

    public ReadOnlyBooleanProperty updateTable() {
        model.getList().clear();
        return this.update(list -> list.stream().collect(Collectors.toList()));
    }

    @Async
    public void onExportExcel(File file, ReportModel model) {
        if (file != null) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {
                    XSSFSheet firstSheet = xssfWorkbook.createSheet("Batch details");
                    firstSheet.setAutobreaks(true);
                    XSSFFont font = xssfWorkbook.createFont();
                    font.setBold(true);
                    XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
                    cellStyle.setFont(font);
                    cellStyle.setAlignment(HorizontalAlignment.LEFT);
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
                    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    XSSFCellStyle valueCellStyle = xssfWorkbook.createCellStyle();
                    valueCellStyle.setAlignment(HorizontalAlignment.LEFT);
                    valueCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    XSSFRow batchNameRow = firstSheet.createRow(0);
                    XSSFCell batchNameLabel = batchNameRow.createCell(0, CellType.STRING);
                    batchNameLabel.setCellValue("Batch Name ");
                    batchNameLabel.setCellStyle(cellStyle);
                    XSSFCell batchName = batchNameRow.createCell(1, CellType.STRING);
                    batchName.setCellValue(model.getBatchName());
                    batchName.setCellStyle(valueCellStyle);
                    XSSFRow batchIdLabelRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell batchIdLabel = batchIdLabelRow.createCell(0, CellType.STRING);
                    batchIdLabel.setCellValue("Batch ID ");
                    batchIdLabel.setCellStyle(cellStyle);
                    XSSFCell batchId = batchIdLabelRow.createCell(1, CellType.NUMERIC);
                    batchId.setCellValue((double) model.getBatchID());
                    batchId.setCellStyle(valueCellStyle);
                    XSSFRow productLabelRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell productLabel = productLabelRow.createCell(0, CellType.STRING);
                    productLabel.setCellValue("Product ");
                    productLabel.setCellStyle(cellStyle);
                    XSSFCell product = productLabelRow.createCell(1, CellType.NUMERIC);
                    product.setCellValue(model.getProduct());
                    product.setCellStyle(valueCellStyle);
                    XSSFRow clientLabelRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell clientLabel = clientLabelRow.createCell(0, CellType.STRING);
                    clientLabel.setCellValue("Client ");
                    clientLabel.setCellStyle(cellStyle);
                    XSSFCell client = clientLabelRow.createCell(1, CellType.NUMERIC);
                    client.setCellValue(model.getClient());
                    client.setCellStyle(valueCellStyle);
                    XSSFRow commentLabelRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell commentLabel = commentLabelRow.createCell(0, CellType.STRING);
                    commentLabel.setCellValue("Comment ");
                    commentLabel.setCellStyle(cellStyle);
                    XSSFCell comment = commentLabelRow.createCell(1, CellType.NUMERIC);
                    comment.setCellValue(model.getComment());
                    comment.setCellStyle(valueCellStyle);
                    XSSFRow creationByRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell creationByLabel = creationByRow.createCell(0, CellType.STRING);
                    creationByLabel.setCellValue("Created by ");
                    creationByLabel.setCellStyle(cellStyle);
                    XSSFCell creationBy = creationByRow.createCell(1, CellType.STRING);
                    creationBy.setCellValue(model.getCreatedBy() == null ? "" : model.getCreatedBy().toString());
                    creationBy.setCellStyle(valueCellStyle);
                    XSSFRow creationDateRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell creationDateLabel = creationDateRow.createCell(0, CellType.STRING);
                    creationDateLabel.setCellValue("Creation date ");
                    creationDateLabel.setCellStyle(cellStyle);
                    XSSFCell creationDate = creationDateRow.createCell(1, CellType.STRING);
                    creationDate.setCellValue(model.getCreationDate() == null ? "" : model.getCreationDate().toString());
                    creationDate.setCellStyle(valueCellStyle);
                    XSSFRow creationTimeRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell creationTimeLabel = creationTimeRow.createCell(0, CellType.STRING);
                    creationTimeLabel.setCellValue("Creation time ");
                    creationTimeLabel.setCellStyle(cellStyle);
                    XSSFCell creationTime = creationTimeRow.createCell(1, CellType.STRING);
                    creationTime.setCellValue(model.getCreationTime() == null ? "" : model.getCreationTime().toString());
                    creationTime.setCellStyle(valueCellStyle);
                    XSSFRow endTimeRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFCell endTimeLabel = endTimeRow.createCell(0, CellType.STRING);
                    endTimeLabel.setCellValue("End time ");
                    endTimeLabel.setCellStyle(cellStyle);
                    XSSFCell endTime = endTimeRow.createCell(1, CellType.STRING);
                    endTime.setCellValue(model.getEndTime() == null ? "" : model.getEndTime().toString());
                    endTime.setCellStyle(valueCellStyle);
                    firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    XSSFRow headerRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                    headerRow.setHeight((short) 400);

                    for (int column = 0; column < MaterialConsumptionController.InHeader.values().length; ++column) {
                        XSSFCell cell = headerRow.createCell(column, CellType.STRING);
                        cell.setCellValue(String.valueOf(MaterialConsumptionController.InHeader.values()[column]));
                        cell.setAsActiveCell();
                        cell.setCellStyle(cellStyle);
                    }

                    for (ReportTableDataModel component : model.getData()) {
                        XSSFRow newRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                        XSSFCell cell0 = newRow.createCell(0, CellType.NUMERIC);
                        cell0.setCellValue(String.valueOf(component.getNumber()));
                        cell0.setCellStyle(valueCellStyle);
                        XSSFCell cell1 = newRow.createCell(1, CellType.STRING);
                        cell1.setCellValue(component.getMaterialName());
                        cell1.setCellStyle(valueCellStyle);
                        XSSFCell cell2 = newRow.createCell(2, CellType.NUMERIC);
                        cell2.setCellValue(String.valueOf(component.getRequired()));
                        cell2.setCellStyle(valueCellStyle);
                        XSSFCell cell3 = newRow.createCell(3, CellType.NUMERIC);
                        cell3.setCellValue(String.valueOf(component.getLoaded()));
                        cell3.setCellStyle(valueCellStyle);
                        XSSFCell cell4 = newRow.createCell(4, CellType.NUMERIC);
                        cell4.setCellValue(component.getError());
                        cell4.setCellStyle(valueCellStyle);
                        XSSFCell cell5 = newRow.createCell(5, CellType.NUMERIC);
                        cell5.setCellValue(component.getRequiredPercent());
                        cell5.setCellStyle(valueCellStyle);
                        XSSFCell cell6 = newRow.createCell(6, CellType.NUMERIC);
                        cell6.setCellValue(component.getActualPercent());
                        cell6.setCellStyle(valueCellStyle);
                    }

                    for (int i = 0; i < MaterialConsumptionController.InHeader.values().length; ++i) {
                        firstSheet.autoSizeColumn(i);
                    }

                    xssfWorkbook.write(fileOutputStream);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public CompletableFuture<Pane> onReport(ReportModel model) {

        if (model.getBatchID() > 0 && !model.getData().isEmpty()) {
            try {
                List<DTO> collect = model.getData().stream()
                        .map(listItem -> new Batches(
                                        String.valueOf(model.getBatchID()),
                                        String.valueOf(model.getBatchName()),
                                        String.valueOf(model.getProduct()),
                                        String.valueOf(model.getEndTime()),
                                        String.valueOf(model.getComment()),
                                        String.valueOf(model.getCreatedBy()),
                                        String.valueOf(model.getCreationDate()),
                                        String.valueOf(model.getCreationTime()),
                                        String.valueOf(model.getClient()),
                                        String.valueOf(listItem.getNumber()),
                                        String.valueOf(listItem.getMaterialName()),
                                        String.valueOf(listItem.getRequired()),
                                        String.valueOf(listItem.getLoaded()),
                                        String.valueOf(listItem.getError()),
                                        String.valueOf(listItem.getRequiredPercent()),
                                        String.valueOf(listItem.getActualPercent())
                                )
                        ).collect(Collectors.toList());

                Pane reportPane = reportDetailsFactory.getReportDetailsPaneFor("Batch", collect);

                return CompletableFuture.completedFuture(reportPane);
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        } else
            return CompletableFuture.failedFuture(new IllegalStateException("Please select a batch"));
    }


    private enum InHeader {
        Number,
        MaterialName,
        Required,
        Loaded,
        Error,
        RequiredPercentage,
        ActualPercentage
    }
}
