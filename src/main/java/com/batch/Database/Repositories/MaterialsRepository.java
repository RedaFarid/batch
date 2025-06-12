
package com.batch.Database.Repositories;

import com.batch.Database.Entities.Material;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialsRepository extends CrudRepository<Material, Long> {
    Material findByName(String name);
}
