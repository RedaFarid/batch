
package com.batch.Database.Repositories;

import com.batch.Database.Entities.TreeViewItemsData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeViewItemsDataRepository extends CrudRepository<TreeViewItemsData, Long> {
}
