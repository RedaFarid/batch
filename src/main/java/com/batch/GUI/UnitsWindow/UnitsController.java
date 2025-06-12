

package com.batch.GUI.UnitsWindow;

import com.batch.Database.Entities.Unit;
import com.batch.Database.Repositories.UnitsRepository;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class UnitsController {
    private static final Logger log = LogManager.getLogger(UnitsController.class);
    private final UnitsModel model = new UnitsModel();
    private final UnitsRepository unitsRepository;

    public UnitsModel getModel() {
        return this.model;
    }

    @Scheduled(
            fixedDelay = 1000L
    )
    public void update() {
        try {
            if (this.model.getIsShown().getValue()) {
                List<Unit> dataBaseList = Lists.newArrayList(this.unitsRepository.findAll());
                Task<Boolean> updateTask = this.updateTask(dataBaseList, this.model.getList());
                Platform.runLater(updateTask);
                updateTask.get();
            }
        } catch (Exception e) {
            log.fatal(e, e);
        }

    }

    private Task<Boolean> updateTask(final List<Unit> dataBaseList, final ObservableList<Unit> dataList) {
        return new Task<Boolean>() {
            protected Boolean call() throws Exception {
                dataList.removeAll((Collection)((ObservableList)dataBaseList.stream().filter((item) -> !dataList.contains(item)).collect(() -> dataList, List::add, List::addAll)).stream().filter((tableListItem) -> dataBaseList.stream().noneMatch((dataBaseItem) -> dataBaseItem.equals(tableListItem))).collect(Collectors.toList()));
                return true;
            }
        };
    }

    public boolean isUnitExist(Unit unit) {
        return this.unitsRepository.existsByName(unit.getName());
    }

    public void saveUnit(Unit unit) {
        this.unitsRepository.save(unit);
    }

    public UnitsController(final UnitsRepository unitsRepository) {
        this.unitsRepository = unitsRepository;
    }
}
