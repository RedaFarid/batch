package com.batch.Database.Repositories;

import com.batch.Database.Entities.BatchControllerData;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchControllerDataRepository extends CrudRepository<BatchControllerData, Long> {
    @Modifying
    @Query("Update BatchControllerData SET LockGeneralControl = :b WHERE Unit like :unitName")
    void updateLockGeneralControl(boolean b, String unitName);

    @Modifying
    @Query("Update BatchControllerData SET CurrentParallelStepsNo = :currentParallelStepsNo, ControlBit = :controlBit  WHERE Unit like :unit")
    void updateForBatchController(int currentParallelStepsNo, boolean controlBit, String unit);

    @Query("SELECT * FROM BatchControllerData where Unit like :unitName ")
    Optional<BatchControllerData> findByUnitName(String unitName);
}
