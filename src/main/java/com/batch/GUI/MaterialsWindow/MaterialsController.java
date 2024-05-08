package com.batch.GUI.MaterialsWindow;

import com.batch.Database.Entities.Material;
import com.batch.Database.Repositories.MaterialsRepository;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MaterialsController {


    private final MaterialsModel model = new MaterialsModel();
    private final MaterialsRepository materialsRepository;

    public MaterialsModel getModel() {
        return model;
    }



    @Scheduled(fixedDelay = 1000)
    public void update() {
        try {
            if (model.getIsShown().getValue()) {
                List<Material> dataBaseList = materialsRepository.findAll();
                Map<Long, Material> dataMap = dataBaseList
                        .stream()
                        .collect(Collectors.toMap(Material::getId, Function.identity()));

                Task<Boolean> updateTask = updateTask(dataMap, dataBaseList, model.getList());
                Platform.runLater(updateTask);
                updateTask.get();
            }
        }catch (Exception e){
            log.fatal(e, e);
        }
    }
    private Task<Boolean> updateTask(Map<Long, Material> dataMap,
                                     List<Material> dataBaseList,
                                     ObservableList<Material> dataList) {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                dataList.removeAll(dataBaseList.stream()
                        .filter(item -> !dataList.contains(item))
                        .collect(() -> dataList, ObservableList::add, ObservableList::addAll)
                        .stream()
                        .filter(tableListItem -> dataBaseList.stream().noneMatch(dataBaseItem -> dataBaseItem.equals(tableListItem)))
                        .collect(Collectors.toList()));

                dataList.forEach(tableRecorde -> {
                    Material databaseRecord = dataMap.get(tableRecorde.getId());

                    tableRecorde.setComment(databaseRecord.getComment());
                    tableRecorde.setName(databaseRecord.getName());
                });

                return true;
            }
        };
    }



    public void save(Material material) {
        materialsRepository.save(material);
    }
    public void delete(String text) {
        materialsRepository.deleteById(Long.parseLong(text));
    }
}
