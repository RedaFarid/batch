
package com.batch.Database.Repositories;

import com.batch.Database.Entities.Recipe;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RecipesRepository extends PagingAndSortingRepository<Recipe, Long> {
}
