package com.batch.Database.Services;

import com.batch.Database.Entities.BatchControllerData;
import com.batch.Database.Repositories.BatchControllerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class BatchControllerDataService {

    @Autowired
    BatchControllerDataRepository batchControllerDataRepository;

    public void updateLockGeneralControl(boolean b, String unitName) {
        batchControllerDataRepository.updateLockGeneralControl(b, unitName);
    }

    public void updateForBatchController(BatchControllerData data) {
        batchControllerDataRepository.updateForBatchController(data.getCurrentParallelStepsNo(), data.isControlBit(), data.getUnit());
    }


    public List<BatchControllerData> findAll() {
        return batchControllerDataRepository.findAll();
    }

    public Optional<BatchControllerData> findById(String unitName) {
        return batchControllerDataRepository.findByUnitName(unitName);
    }

    public void save(BatchControllerData data) {
        batchControllerDataRepository.save(data);
    }
}
