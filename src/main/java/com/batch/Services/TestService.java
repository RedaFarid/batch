package com.batch.Services;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Entities.TagLog;
import com.batch.Database.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//@Service
public class TestService {

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private ParametersRepository parametersRepository;

    @Autowired
    private BatchesRepository batchesRepository;

    @Autowired
    private RecipesRepository recipesRepository;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private TreeViewItemsDataRepository treeViewItemsDataRepository;

    @Autowired
    private UnitsRepository unitsRepository;

    @Autowired
    private BatchControllerDataRepository batchControllerDataRepository;

    @Autowired
    private TagLogRepository tagLogRepository;

    @Async
    @EventListener
    public void atStarted(ContextStartedEvent event){
        try {
            batchesRepository.findAll()
                    .parallelStream()
                    .map(Batch::getModel)
                    .flatMap(item -> item.getParallelSteps().parallelStream())
                    .flatMap(item -> item.getSteps().parallelStream())
                    .forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            recipesRepository.findAll()
                    .parallelStream()
                    .map(Recipe::getModel)
                    .flatMap(item -> item.getParallelSteps().parallelStream())
                    .flatMap(item -> item.getSteps().parallelStream())
                    .forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            phaseRepository.findAll().forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            materialsRepository.findAll().forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            treeViewItemsDataRepository.findAll().forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            unitsRepository.findAll().forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            batchControllerDataRepository.findAll().forEach(System.err::println);
            System.err.println("--------------------------------------------------------------------------------------");
            tagLogRepository.findAllIterable().forEach(System.err::println);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
