package com.batch.Database.Repositories;

import com.batch.Database.Entities.Unit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitsRepository extends CrudRepository<Unit, Long> {
    boolean existsByName(String name);
}
