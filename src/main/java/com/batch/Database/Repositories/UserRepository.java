package com.batch.Database.Repositories;

import com.batch.Database.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {


    boolean existsByUserName(String currentUser);
    User findByUserName(String userName);
    void deleteByUserName(String userName);
}
