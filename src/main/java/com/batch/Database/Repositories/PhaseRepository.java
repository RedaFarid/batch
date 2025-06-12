package com.batch.Database.Repositories;

import com.batch.Database.Entities.Phase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseRepository extends CrudRepository<Phase, Long> {
}
