package com.batch.Database.Repositories;

import com.batch.Database.Entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Query(value = "select * from log where id <?1", nativeQuery = true)
    List<Log> getLogsTillID(long greaterID);

    @Query(value = "select top 1 * from [log] order by id desc", nativeQuery = true)
    Optional<Log> findLast();
}
