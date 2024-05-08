package com.batch.GUI.Reporting;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Services.BatchesService;
import com.batch.GUI.Reporting.Reports.ReportModel;
import com.batch.GUI.Reporting.Reports.ReportTableDataModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ReportsController {

    private final MaterialsRepository materialsRepository;
    private final BatchesService batchesService;

    private final ReportsModel model = new ReportsModel();

    public ReportsModel getModel() {
        return model;
    }

    private void update(List<Batch> dataBaseList) {
        try {
            Task<Boolean> updateTask = updateTask(dataBaseList, model.getList());
            Platform.runLater(updateTask);
            updateTask.get();
        } catch (Exception e) {
            log.fatal(e, e);
        }
    }

    private Task<Boolean> updateTask(List<Batch> dataBaseList,
                                     ObservableList<Batch> dataList) {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                dataList.removeAll(dataBaseList.stream()
                        .filter(item -> !dataList.contains(item))
                        .collect(() -> dataList, ObservableList::add, ObservableList::addAll)
                        .stream()
                        .filter(tableListItem -> dataBaseList.stream().noneMatch(dataBaseItem -> dataBaseItem.equals(tableListItem)))
                        .collect(Collectors.toList()));

                return true;
            }
        };
    }

    public Optional<Material> getMaterialById(long materialID) {
        return materialsRepository.findById(materialID);
    }

    @Async
    public void onFilterByDate(MouseEvent mouseEvent) {
        if (model.getFromDate().getValue() != null && model.getToDate().getValue() != null) {
            update(
                    batchesService
                            .findAll()
                            .stream()
                            .filter(item -> item.getCreationDate().isAfter(model.getFromDate().getValue()))
                            .filter(item -> item.getCreationDate().isBefore(model.getToDate().get()))
                            .collect(Collectors.toList()));
        }
    }

    @Async
    public void onFilterByName(MouseEvent mouseEvent) {
        if (model.getFilterString().getValue() != null) {
            update(
                    batchesService
                            .findAll()
                            .stream()
                            .filter(item -> item.getBatchName().toLowerCase().trim().contains(model.getFilterString().getValue()))
                            .collect(Collectors.toList()));
        }
    }

    @Async
    public void updateTable() {
        update(batchesService.findAll());
    }

//    @Async
//    public void exportReport(ReportModel model, File file) {
//        if (file != null) {
//            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
//                try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {
//                    //Create sheet
//                    XSSFSheet firstSheet = xssfWorkbook.createSheet("Batch details");
//                    firstSheet.setAutobreaks(true);
//
//                    //Defining some styles
//                    XSSFFont font = xssfWorkbook.createFont();
//                    font.setBold(true);
//
//                    XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
//                    cellStyle.setFont(font);
//                    cellStyle.setAlignment(HorizontalAlignment.LEFT);
//                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
////                    cellStyle.setBorderBottom(BorderStyle.MEDIUM);
//                    cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
//                    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//
//                    XSSFCellStyle valueCellStyle = xssfWorkbook.createCellStyle();
//                    valueCellStyle.setAlignment(HorizontalAlignment.LEFT);
//                    valueCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//
//                    //Filling Introduction data
//                    XSSFRow batchNameRow = firstSheet.createRow(0);
//                    XSSFCell batchNameLabel = batchNameRow.createCell(0, CellType.STRING);
//                    batchNameLabel.setCellValue("Batch Name ");
//                    batchNameLabel.setCellStyle(cellStyle);
//                    XSSFCell batchName = batchNameRow.createCell(1, CellType.STRING);
//                    batchName.setCellValue(model.getBatchName());
//                    batchName.setCellStyle(valueCellStyle);
//
//                    XSSFRow batchIdLabelRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
//                    XSSFCell batchIdLabel = batchIdLabelRow.createCell(0, CellType.STRING);
//                    batchIdLabel.setCellValue("Batch ID ");
//                    batchIdLabel.setCellStyle(cellStyle);
//                    XSSFCell batchId = batchIdLabelRow.createCell(1, CellType.NUMERIC);
//                    batchId.setCellValue(model.getBatchID());
//                    batchId.setCellStyle(valueCellStyle);
//
//                    XSSFRow creationDateRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
//                    XSSFCell creationDateLabel = creationDateRow.createCell(0, CellType.STRING);
//                    creationDateLabel.setCellValue("Creation date ");
//                    creationDateLabel.setCellStyle(cellStyle);
//                    XSSFCell creationDate = creationDateRow.createCell(1, CellType.STRING);
//                    creationDate.setCellValue(model.getCreationDate() == null ? "" : model.getCreationDate().toString());
//                    creationDate.setCellStyle(valueCellStyle);
//
//                    XSSFRow creationTimeRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
//                    XSSFCell creationTimeLabel = creationTimeRow.createCell(0, CellType.STRING);
//                    creationTimeLabel.setCellValue("Creation time ");
//                    creationTimeLabel.setCellStyle(cellStyle);
//                    XSSFCell creationTime = creationTimeRow.createCell(1, CellType.STRING);
//                    creationTime.setCellValue(model.getCreationTime() == null ? "" : model.getCreationTime().toString());
//                    creationTime.setCellStyle(valueCellStyle);
//
//                    //Spacing
//                    XSSFRow spacingRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
//                    //....................................................................................................
//
//                    //Create header row
//                    XSSFRow headerRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
//                    headerRow.setHeight(((short) 400));
//
//                    //Fill header row
//                    for (int column = 0; column < InHeader.values().length; column++) {
//                        XSSFCell cell = headerRow.createCell(column, CellType.STRING);
//                        cell.setCellValue(String.valueOf(InHeader.values()[column]));
//                        cell.setAsActiveCell();
//                        cell.setCellStyle(cellStyle);
//                    }
//
//                    //Fill data
//                    for (ReportTableDataModel component : model.getData()) {
//                        XSSFRow newRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
//
//                        XSSFCell cell0 = newRow.createCell(0, CellType.NUMERIC);
//                        cell0.setCellValue(String.valueOf(component.getNumber()));
//                        cell0.setCellStyle(valueCellStyle);
//
//                        XSSFCell cell1 = newRow.createCell(1, CellType.STRING);
//                        cell1.setCellValue(component.getMaterialName());
//                        cell1.setCellStyle(valueCellStyle);
//
//                        XSSFCell cell2 = newRow.createCell(2, CellType.NUMERIC);
//                        cell2.setCellValue(String.valueOf(component.getRequired()));
//                        cell2.setCellStyle(valueCellStyle);
//
//                        XSSFCell cell3 = newRow.createCell(3, CellType.NUMERIC);
//                        cell3.setCellValue(String.valueOf(component.getLoaded()));
//                        cell3.setCellStyle(valueCellStyle);
//
//                        XSSFCell cell4 = newRow.createCell(4, CellType.NUMERIC);
//                        cell4.setCellValue(component.getError());
//                        cell4.setCellStyle(valueCellStyle);
//
//                        XSSFCell cell5 = newRow.createCell(5, CellType.NUMERIC);
//                        cell5.setCellValue(component.getRequiredPercent());
//                        cell5.setCellStyle(valueCellStyle);
//
//                        XSSFCell cell6 = newRow.createCell(6, CellType.NUMERIC);
//                        cell6.setCellValue(component.getActualPercent());
//                        cell6.setCellStyle(valueCellStyle);
//                    }
//                    for (int i = 0; i < InHeader.values().length; i++) {
//                        firstSheet.autoSizeColumn(i);
//                    }
//
//                    xssfWorkbook.write(fileOutputStream);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
