
package com.batch.Database.Repositories;

import com.batch.Database.Entities.Log;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends CrudRepository<Log, Long> {
    @Query("select * from log where id < :greaterID")
    List<Log> getLogsTillID(long greaterID);

    @Query("select top 1 * from [log] order by id desc")
    Optional<Log> findLast();
}
