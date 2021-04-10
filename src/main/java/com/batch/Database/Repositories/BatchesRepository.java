package com.batch.Database.Repositories;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BatchesRepository extends JpaRepository<Batch, Long> {

}
