

package com.batch.Database.Repositories;

import com.batch.Database.Entities.Batch;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BatchesRepository extends PagingAndSortingRepository<Batch, Long> {
    Optional<Batch> findByBatchName(String batchName);

    @Modifying
    @Query("update batches set [order] = :order where id = :batchId")
    void updateBatchControlOrder(long batchId, String order);

    @Modifying
    @Query("update batches set endTime = :now where id = :id")
    void updateEndTime(Long id, LocalDateTime now);
}
