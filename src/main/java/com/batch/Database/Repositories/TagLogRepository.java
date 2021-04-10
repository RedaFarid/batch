package com.batch.Database.Repositories;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.TagLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagLogRepository extends JpaRepository<TagLog, Long> {

    @Query(value = "Select * from [TagLog] order by Date desc, time desc", nativeQuery = true)
    Iterable<TagLog> findAllIterable();
}
