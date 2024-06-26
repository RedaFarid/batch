package com.batch.Database.Repositories;

import com.batch.Database.Entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface GroupRepository extends JpaRepository<Group, Long> {


    void deleteByGroup(String group);
    boolean existsByGroup(String group);

    @Modifying
    @Query(value = "UPDATE AuthorizationGroups set [Descreption] = ?2 where GroupName like ?1 ", nativeQuery = true)
    void updateDescriptionByGroup(String group, String desc);
}