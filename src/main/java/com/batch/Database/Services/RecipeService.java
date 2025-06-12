package com.batch.Database.Services;

import com.batch.DTO.RecipeSystemDataDefinitions.RecipeModel;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Repositories.RecipesRepository;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private static final Logger log = LogManager.getLogger(RecipeService.class);
    private final RecipesRepository recipesRepository;

    public RecipeService(final RecipesRepository recipesRepository) {
        this.recipesRepository = recipesRepository;
    }

    @CacheEvict({"recipes"})
    public Optional<Recipe> save(Recipe selectedRecipe) {
        return this.startMarshalling(selectedRecipe.getModel()).map((rowModel) -> {
            selectedRecipe.setRowModel(rowModel);
            return this.recipesRepository.save(selectedRecipe);
        });
    }

    @Cacheable({"recipes"})
    public Optional<Recipe> findById(Long id) {
        return this.recipesRepository.findById(id).flatMap((recipe) -> this.startUnMarshalling(recipe.getRowModel()).map((recipeModel) -> {
            recipe.setModel(recipeModel);
            return recipe;
        }));
    }

    @Cacheable({"recipes"})
    public List<Recipe> findAll() {
        return Lists.newArrayList(this.recipesRepository.findAll()).stream().flatMap((recipe) -> this.startUnMarshalling(recipe.getRowModel()).map((batchModel) -> {
            recipe.setModel(batchModel);
            return recipe;
        }).stream()).collect(Collectors.toList());
    }

    private Optional<String> startMarshalling(RecipeModel model) {
        try {
            StringWriter sw = new StringWriter();
            JAXBContext jaxbcontext = JAXBContext.newInstance(RecipeModel.class);
            Marshaller marshaller = jaxbcontext.createMarshaller();
            marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
            marshaller.marshal(model, sw);
            return Optional.ofNullable(sw.toString());
        } catch (Exception var5) {
            return Optional.empty();
        }
    }

    private Optional<RecipeModel> startUnMarshalling(String model) {
        try {
            JAXBContext jaxbcontext = JAXBContext.newInstance(RecipeModel.class);
            Unmarshaller unMarshaller = jaxbcontext.createUnmarshaller();
            return Optional.ofNullable((RecipeModel) unMarshaller.unmarshal(new StringReader(model)));
        } catch (Exception var4) {
            return Optional.empty();
        }
    }
}
