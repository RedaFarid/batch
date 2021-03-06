package com.batch.Database.Repositories;

import com.batch.Database.Entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitsRepository extends JpaRepository<Unit, Long> {


    boolean existsByName(String name);
}
