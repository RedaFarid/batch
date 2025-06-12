
package com.batch.Database.Repositories;

import com.batch.Database.Entities.TagLog;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagLogRepository extends CrudRepository<TagLog, Long> {
    @Query("Select * from [TagLog] order by Date desc, time desc")
    Iterable<TagLog> findAllIterable();
}
