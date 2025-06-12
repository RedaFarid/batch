
package com.batch.Database.Services;

import com.batch.Database.Entities.RecipeConf;
import com.batch.Database.Repositories.RecipeConfRepository;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RecipeConfigService {
    private static final Logger log = LogManager.getLogger(RecipeConfigService.class);
    private final RecipeConfRepository recipeConfRepository;

    @Cacheable(
            value = {"recipe_config"},
            cacheManager = "cacheManagerForRecipeConfig"
    )
    public List<RecipeConf> findAll() {
        return Lists.newArrayList(this.recipeConfRepository.findAll());
    }

    public RecipeConfigService(final RecipeConfRepository recipeConfRepository) {
        this.recipeConfRepository = recipeConfRepository;
    }
}
