package com.batch.Database.Repositories;

import com.batch.Database.Entities.RecipeConf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeConfRepository extends JpaRepository<RecipeConf, Long> {
}
