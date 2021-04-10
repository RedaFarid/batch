package com.batch.Database.Services;

import com.batch.Database.Entities.BatchControllerData;
import com.batch.Database.Repositories.BatchControllerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchControllerDataService {

    @Autowired
    BatchControllerDataRepository batchControllerDataRepository;

    public void updateLockGeneralControl(boolean b, String unitName) {
        batchControllerDataRepository.updateLockGeneralControl(b, unitName);
    }

    public void update(BatchControllerData data) {
        batchControllerDataRepository.updateForBatchController(data.getCurrentParallelStepsNo(), data.isControlBit(), data.getUnit());
    }

    public void updateForBatchController(BatchControllerData data) {
        batchControllerDataRepository.update(data.getRunningBatchID(), data.getCurrentParallelStepsNo(), data.isControlBit(), data.isLockGeneralControl(), data.getUnit());
    }


    public List<BatchControllerData> findAll() {
        return batchControllerDataRepository.findAll();
    }
}
