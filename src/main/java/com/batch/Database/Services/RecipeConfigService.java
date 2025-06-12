package com.batch.Database.Services;

import com.batch.Database.Entities.RecipeConf;
import com.batch.Database.Repositories.RecipeConfRepository;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeConfigService {
    private static final Logger log = LogManager.getLogger(RecipeConfigService.class);
    private final RecipeConfRepository recipeConfRepository;

    public RecipeConfigService(final RecipeConfRepository recipeConfRepository) {
        this.recipeConfRepository = recipeConfRepository;
    }

    @Cacheable(
            value = {"recipe_config"},
            cacheManager = "cacheManagerForRecipeConfig"
    )
    public List<RecipeConf> findAll() {
        return Lists.newArrayList(this.recipeConfRepository.findAll());
    }
}
