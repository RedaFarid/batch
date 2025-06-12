
package com.batch.Database.Repositories;

import com.batch.Database.Entities.Group;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {
    void deleteByGroup(String group);

    boolean existsByGroup(String group);

    @Modifying
    @Query("UPDATE AuthorizationGroups set [Descreption] = :desc where GroupName like :group ")
    void updateDescriptionByGroup(String group, String desc);
}
