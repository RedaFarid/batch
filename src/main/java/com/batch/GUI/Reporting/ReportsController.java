package com.batch.GUI.Reporting;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Services.BatchesService;
import com.batch.GUI.Reporting.Reports.ReportModel;
import com.batch.GUI.Reporting.Reports.ReportTableDataModel;
import com.batch.Reporting.ReportsDTO.Batches;
import com.batch.Reporting.ReportsDTO.DTO;
import com.batch.Reporting.ReportsDetails.ReportDetailsFactory;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class ReportsController {
    private final MaterialsRepository materialsRepository;
    private final BatchesService batchesService;
    private final TaskExecutor executor;
    private final ReportsModel model = new ReportsModel();
    @Autowired
    private ReportDetailsFactory reportDetailsFactory;

    public ReportsController(final MaterialsRepository materialsRepository, final BatchesService batchesService, final TaskExecutor executor) {
        this.materialsRepository = materialsRepository;
        this.batchesService = batchesService;
        this.executor = executor;
    }

    public ReportsModel getModel() {
        return this.model;
    }

    private ReadOnlyBooleanProperty update(Function<List<Batch>, List<Batch>> function) {
        Task<Boolean> updateTask = this.updateTask(function);
        this.executor.execute(updateTask);
        return updateTask.runningProperty();
    }

    private Task<Boolean> updateTask(final Function<List<Batch>, List<Batch>> function) {
        return new Task<Boolean>() {
            protected Boolean call() throws Exception {
                ObservableList<Batch> dataList = ReportsController.this.model.getList();
                List<Batch> dataBaseList = function.apply(ReportsController.this.batchesService.findAll());
                Platform.runLater(() -> dataList.removeAll((Collection) ((ObservableList) dataBaseList.stream().filter((item) -> !dataList.contains(item)).collect(() -> dataList, List::add, List::addAll)).stream().filter((tableListItem) -> dataBaseList.stream().noneMatch((dataBaseItem) -> dataBaseItem.equals(tableListItem))).collect(Collectors.toList())));
                return true;
            }
        };
    }

    public ReadOnlyBooleanProperty onFilterByDate() {
        if (this.model.getFromDate().getValue() != null && this.model.getToDate().getValue() != null) {
            this.model.getList().clear();
            return this.update(list -> list.stream().filter((item) -> item.getCreationDate().isAfter(this.model.getFromDate().getValue())).filter((item) -> item.getCreationDate().isBefore(this.model.getToDate().get())).sorted(Comparator.comparing(Batch::getId).reversed()).collect(Collectors.toList()));
        } else {
            return new SimpleBooleanProperty(false);
        }
    }

    public ReadOnlyBooleanProperty onFilterByName() {
        if (this.model.getFilterString().getValue() != null) {
            this.model.getList().clear();
            return this.update(list -> list.stream().filter((item) -> item.getBatchName().toLowerCase().trim().contains(this.model.getFilterString().getValue())).sorted(Comparator.comparing(Batch::getId).reversed()).collect(Collectors.toList()));
        } else {
            return new SimpleBooleanProperty(false);
        }
    }

    public ReadOnlyBooleanProperty updateTable() {
        this.model.getList().clear();
        return this.update(list -> list.stream().sorted(Comparator.comparing(Batch::getId).reversed()).collect(Collectors.toList()));
    }

    public Optional<Material> getMaterialById(long materialID) {
        return this.materialsRepository.findById(materialID);
    }

    @Async
    public void exportReport(ReportModel model, File file, Runnable showWindow) {
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

                    for (int column = 0; column < ReportsController.InHeader.values().length; ++column) {
                        XSSFCell cell = headerRow.createCell(column, CellType.STRING);
                        cell.setCellValue(String.valueOf(ReportsController.InHeader.values()[column]));
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

                    for (int i = 0; i < ReportsController.InHeader.values().length; ++i) {
                        firstSheet.autoSizeColumn(i);
                    }

                    xssfWorkbook.write(fileOutputStream);
                }

                showWindow.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

                    for (int column = 0; column < ReportsController.InHeader.values().length; ++column) {
                        XSSFCell cell = headerRow.createCell(column, CellType.STRING);
                        cell.setCellValue(String.valueOf(ReportsController.InHeader.values()[column]));
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

                    for (int i = 0; i < ReportsController.InHeader.values().length; ++i) {
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

                return CompletableFuture.completedFuture(new Pane());
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
