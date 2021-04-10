package com.batch.Database.Repositories;

import com.batch.Database.Entities.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametersRepository extends JpaRepository<Parameter, Long> {
}
