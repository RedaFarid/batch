package com.batch.GUI.MaterialsWindow;

import com.batch.Database.Entities.Material;
import com.batch.Database.Repositories.MaterialsRepository;
import com.google.common.collect.Lists;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class MaterialsController {
    private static final Logger log = LogManager.getLogger(MaterialsController.class);
    private final MaterialsModel model = new MaterialsModel();
    private final MaterialsRepository materialsRepository;

    public MaterialsController(final MaterialsRepository materialsRepository) {
        this.materialsRepository = materialsRepository;
    }

    public MaterialsModel getModel() {
        return this.model;
    }

    @Scheduled(
            fixedDelay = 1000L
    )
    public void update() {
        try {
            if (this.model.getIsShown().getValue()) {
                List<Material> dataBaseList = Lists.newArrayList(this.materialsRepository.findAll());
                Map<Long, Material> dataMap = dataBaseList.stream().collect(Collectors.toMap(Material::getId, Function.identity()));
                Task<Boolean> updateTask = this.updateTask(dataMap, dataBaseList, this.model.getList());
                Platform.runLater(updateTask);
                updateTask.get();
            }
        } catch (Exception e) {
            log.fatal(e, e);
        }

    }

    private Task<Boolean> updateTask(final Map<Long, Material> dataMap, final List<Material> dataBaseList, final ObservableList<Material> dataList) {
        return new Task<Boolean>() {
            protected Boolean call() throws Exception {
                dataList.removeAll((Collection) ((ObservableList) dataBaseList.stream().filter((item) -> !dataList.contains(item)).collect(() -> dataList, List::add, List::addAll)).stream().filter((tableListItem) -> dataBaseList.stream().noneMatch((dataBaseItem) -> dataBaseItem.equals(tableListItem))).collect(Collectors.toList()));
                dataList.forEach((tableRecorde) -> {
                    Material databaseRecord = dataMap.get(tableRecorde.getId());
                    tableRecorde.setComment(databaseRecord.getComment());
                    tableRecorde.setName(databaseRecord.getName());
                });
                return true;
            }
        };
    }

    public void save(Material material) {
        this.materialsRepository.save(material);
    }

    public void delete(String text) {
        this.materialsRepository.deleteById(Long.parseLong(text));
    }
}
