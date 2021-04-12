package com.batch.GUI.Reporting;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Material;
import com.batch.Database.Repositories.MaterialsRepository;
import com.batch.Database.Services.BatchesService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

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
        if (model.getFromDate().getValue() != null && model.getToDate().getValue() != null){
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
        if (model.getFilterString().getValue() != null){
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
        System.err.println("Report show state change");
        update(batchesService.findAll());
    }
}
