
package com.batch.Database.Services;

import com.batch.Database.Entities.BatchControllerData;
import com.batch.Database.Repositories.BatchControllerDataRepository;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(
        isolation = Isolation.SERIALIZABLE,
        propagation = Propagation.REQUIRES_NEW
)
public class BatchControllerDataService {
    @Autowired
    BatchControllerDataRepository batchControllerDataRepository;

    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW
    )
    public void updateLockGeneralControl(boolean b, String unitName) {
        this.batchControllerDataRepository.updateLockGeneralControl(b, unitName);
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW
    )
    public void updateForBatchController(BatchControllerData data) {
        this.batchControllerDataRepository.updateForBatchController(data.getCurrentParallelStepsNo(), data.isControlBit(), data.getUnit());
    }

    public List<BatchControllerData> findAll() {
        return Lists.newArrayList(this.batchControllerDataRepository.findAll());
    }

    public Optional<BatchControllerData> findById(String unitName) {
        return this.batchControllerDataRepository.findByUnitName(unitName);
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ,
            propagation = Propagation.REQUIRES_NEW
    )
    public void save(BatchControllerData data) {
        this.batchControllerDataRepository.save(data);
    }
}
