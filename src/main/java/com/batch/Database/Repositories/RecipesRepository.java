package com.batch.Database.Repositories;

import com.batch.Database.Entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipesRepository extends JpaRepository<Recipe, Long> {
}
