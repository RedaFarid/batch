package com.batch.Database.Services;

import com.batch.Database.Entities.RecipeConf;
import com.batch.Database.Repositories.RecipeConfRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class RecipeConfigService {
    private final RecipeConfRepository recipeConfRepository;

    @Cacheable(value = "recipe_config", cacheManager = "cacheManagerForRecipeConfig")
    public List<RecipeConf> findAll() {
        return recipeConfRepository.findAll();
    }
}
