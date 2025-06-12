
package com.batch.Database.Repositories;

import com.batch.Database.Entities.Unit;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UnitsRepository extends PagingAndSortingRepository<Unit, Long> {
    boolean existsByName(String name);
}
