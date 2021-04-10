package com.batch.Database.Repositories;

import com.batch.Database.Entities.BatchControllerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BatchControllerDataRepository extends JpaRepository<BatchControllerData, Long> {


    @Query(value = "Update BatchControllerData SET LockGeneralControl = ?1 WHERE Unit ?2", nativeQuery = true)
    void updateLockGeneralControl(boolean b, String unitName);

    @Query(value = "Update BatchControllerData SET runningBatchID = ?1, CurrentParallelStepsNo = ?2, ControlBit = ?3, LockGeneralControl = ?4 WHERE Unit = ?5", nativeQuery = true)
    void update(long r, int psn, boolean cb, boolean lgc, String unit);

    @Query(value = "Update BatchControllerData SET LockGeneralControl = ?1, ControlBit = ?2  WHERE Unit ?3", nativeQuery = true)
    void updateForBatchController(int currentParallelStepsNo, boolean controlBit, String unit);

}


