

package com.batch.Database.Repositories;

import com.batch.Database.Entities.Parameter;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ParametersRepository extends PagingAndSortingRepository<Parameter, Long> {
}
