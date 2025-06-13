package com.batch.Database.Repositories;

import com.batch.Database.Entities.Log;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends CrudRepository<Log, Long> {
    @Query("select * from log where id < :greaterID")
    List<Log> getLogsTillID(long greaterID);

    @Query("select top 1 * from [log] order by id desc")
    Optional<Log> findLast();

    @Query("select * from [log] order by [Date] desc, [Time] desc")
    LinkedList<Log> findAll();

    @Query("select top 1 * from [log] where source = :source order by [ID] asc")
    Optional<Log> findLastBySource(String source);
}
