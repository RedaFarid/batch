package com.batch.Database.Repositories;

import com.batch.Database.Entities.Batch;
import com.batch.Database.Entities.TreeViewItemsData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeViewItemsDataRepository extends JpaRepository<TreeViewItemsData, Long> {
}
