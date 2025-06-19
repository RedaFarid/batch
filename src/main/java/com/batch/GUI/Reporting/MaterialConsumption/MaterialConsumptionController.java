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
import javafx.scene.control.TableView;
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
                    .filter((item)->  item.getCreationDate()!=null)
                    .filter((item) -> item.getCreationDate().isAfter(model.getFromDate().getValue())  ||  item.getCreationDate().isEqual(model.getFromDate().getValue()))
                    .filter((item) -> item.getCreationDate().isBefore(model.getToDate().get())  ||  item.getCreationDate().isEqual(model.getToDate().getValue()))
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
                                    return new ReportTableDataModel(0, "", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                                }
                            }))).collect(Collectors.toList());
            counter = 1;
            data = data.stream().map((item) -> new ReportTableDataModel(counter++, item.getMaterialName(), item.getRequired(), item.getLoaded(), item.getError(), 0.0, 0.0)).collect(Collectors.toList());
            Map<String, MaterialConsumptionModel.Item> map = new HashMap<>();
            data.stream().filter(i->i.getMaterialName()!="").forEach(item -> {
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
            map.forEach((k,v)->{
                list.add(v);});
        }
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
    public void onExportExcel(File file, ObservableList<MaterialConsumptionModel.Item>  list) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {
                XSSFSheet firstSheet = xssfWorkbook.createSheet("Material Consumption");
                XSSFRow row = firstSheet.createRow(0);
                firstSheet.setAutobreaks(true);
                XSSFFont font = xssfWorkbook.createFont();
                font.setBold(true);

                firstSheet.setAutobreaks(true);
                row.setHeight(((short) 400));

                XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
                cellStyle.setFont(font);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
                cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

                XSSFCellStyle valueCellStyle = xssfWorkbook.createCellStyle();
                valueCellStyle.setAlignment(HorizontalAlignment.LEFT);
                valueCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


                XSSFRow fromDateRow = firstSheet.createRow(0);
                XSSFCell fromLabel = fromDateRow.createCell(0, CellType.STRING);
                fromLabel.setCellValue("From ");
                fromLabel.setCellStyle(cellStyle);
                XSSFCell from = fromDateRow.createCell(1, CellType.STRING);
                from.setCellValue(model.getFromDate().getValue().toString());
                from.setCellStyle(valueCellStyle);

                XSSFRow toDateRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                XSSFCell toLabel = toDateRow.createCell(0, CellType.STRING);
                toLabel.setCellValue("To ");
                toLabel.setCellStyle(cellStyle);
                XSSFCell to = toDateRow.createCell(1, CellType.STRING);
                to.setCellValue( model.getToDate().getValue().toString());
                to.setCellStyle(valueCellStyle);

                firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                XSSFRow headerRow = firstSheet.createRow(firstSheet.getLastRowNum() + 1);
                headerRow.setHeight((short) 400);

                //Fill header row
                for (int column = 0; column < 4; column++) {
                    XSSFCell cell = headerRow.createCell(column, CellType.STRING);
                    cell.setCellValue(String.valueOf(InHeader.values()[column]));
                    cell.setAsActiveCell();
                    cell.setCellStyle(cellStyle);
                }

                //Fill data
                list.forEach(item -> {

                            XSSFRow row1 = firstSheet.createRow(firstSheet.getLastRowNum() + 1);

                            XSSFCell cell0 = row1.createCell(0, CellType.STRING);
                            cell0.setCellValue(String.valueOf(item.getMaterialName()));

                            XSSFCell cell01 = row1.createCell(1, CellType.NUMERIC);
                            cell01.setCellValue(item.getRequired());

                            XSSFCell cell1 = row1.createCell(2, CellType.NUMERIC);
                            cell1.setCellValue(item.getActual());

                            XSSFCell cell2 = row1.createCell(3, CellType.NUMERIC);
                            cell2.setCellValue(item.getError());

                        }
                );
                firstSheet.autoSizeColumn(0);
                firstSheet.autoSizeColumn(1);
                firstSheet.autoSizeColumn(2);
                firstSheet.autoSizeColumn(3);

                xssfWorkbook.write(fileOutputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        MaterialName,
        Required,
        Loaded,
        Error
    }
}
