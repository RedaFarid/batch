package com.batch.GUI.UnitsWindow;

import com.batch.Database.Entities.Unit;
import com.batch.Database.Repositories.UnitsRepository;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class UnitsController {

    private final UnitsModel model = new UnitsModel();
    private final UnitsRepository unitsRepository;


    public UnitsModel getModel() {
        return model;
    }

    @Scheduled(fixedDelay = 1000)
    public void update() {
        try {
            if (model.getIsShown().getValue()) {
                List<Unit> dataBaseList = unitsRepository.findAll();

                Task<Boolean> updateTask = updateTask(dataBaseList, model.getList());
                Platform.runLater(updateTask);
                updateTask.get();
            }
        }catch (Exception e){
            log.fatal(e, e);
        }
    }
    private Task<Boolean> updateTask(List<Unit> dataBaseList,
                                     ObservableList<Unit> dataList) {
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

    public boolean isUnitExist(Unit unit) {
        return unitsRepository.existsByName(unit.getName());
    }

    public void saveUnit(Unit unit) {
        unitsRepository.save(unit);
    }
}
