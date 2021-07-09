package com.batch.Database.Repositories;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface BatchesRepository extends JpaRepository<Batch, Long> {

    Optional<Batch> findByBatchName(String batchName);


    @Modifying
    @Query(value = "update batches set [order] = ?2 where id = ?1", nativeQuery = true)
    void updateBatchControlOrder(long batchId, String order);
}
