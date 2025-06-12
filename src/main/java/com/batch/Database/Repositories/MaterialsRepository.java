
package com.batch.Database.Repositories;

import com.batch.Database.Entities.Material;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MaterialsRepository extends PagingAndSortingRepository<Material, Long> {
    Material findByName(String name);
}
