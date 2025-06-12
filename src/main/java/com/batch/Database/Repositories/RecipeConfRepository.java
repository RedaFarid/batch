package com.batch.Database.Repositories;

import com.batch.Database.Entities.RecipeConf;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeConfRepository extends CrudRepository<RecipeConf, Long> {
}
