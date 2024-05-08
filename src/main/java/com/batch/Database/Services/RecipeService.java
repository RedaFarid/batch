package com.batch.Database.Services;

import com.batch.DTO.RecipeSystemDataDefinitions.RecipeModel;
import com.batch.Database.Entities.Recipe;
import com.batch.Database.Repositories.RecipesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipesRepository recipesRepository;

    @CacheEvict("recipes")
    public Recipe save(Recipe selectedRecipe) {
        final String rowModel = toRowModel(selectedRecipe.getModel());
        selectedRecipe.setRowModel(rowModel);
        return recipesRepository.save(selectedRecipe);
    }

    @Cacheable("recipes")
    public Optional<Recipe> findById(Long id) {
        return recipesRepository
                .findById(id)
                .map(recipe -> {
                    final RecipeModel recipeModel = toModel(recipe.getRowModel());
                    recipe.setModel(recipeModel);
                    return recipe;
                });
    }

    @Cacheable("recipes")
    public List<Recipe> findAll() {
        return recipesRepository.findAll();
    }



    //Managing XML
    public RecipeModel toModel(String rowModel) {
        RecipeModel recipeModel = new RecipeModel();
        try {
            recipeModel = startUnMarshalling(rowModel);
        } catch (Exception ignored) {

        }
        return recipeModel;
    }
    public String toRowModel(RecipeModel model) {
        String s = "";
        try {
            s = startMarshalling(model);
        } catch (Exception ignored) {
        }
        return s;
    }

    private String startMarshalling(RecipeModel model) throws Exception {
        StringWriter sw = new StringWriter();
        JAXBContext jaxbcontext = JAXBContext.newInstance(RecipeModel.class);
        Marshaller marshaller = jaxbcontext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.marshal(model, sw);
        return sw.toString();
    }
    private RecipeModel startUnMarshalling(String model) throws Exception {
        JAXBContext jaxbcontext = JAXBContext.newInstance(RecipeModel.class);
        Unmarshaller unMarshaller = jaxbcontext.createUnmarshaller();
        return ((RecipeModel) unMarshaller.unmarshal(new StringReader(model)));
    }


}
