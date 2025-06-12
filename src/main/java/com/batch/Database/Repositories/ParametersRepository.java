package com.batch.Database.Repositories;

import com.batch.Database.Entities.Parameter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametersRepository extends CrudRepository<Parameter, Long> {
}
