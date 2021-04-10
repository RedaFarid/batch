package com.batch.Database.Repositories;

import com.batch.Database.Entities.Log;
import com.batch.Database.Entities.TagLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Query(value = "select * from log where id <?1", nativeQuery = true)
    List<Log> getLogsTillID(long greaterID);
}
