package com.batch.Database.Repositories;

import com.batch.Database.Entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUserName(String currentUser);

    User findByUserName(String userName);

    void deleteByUserName(String userName);
}
