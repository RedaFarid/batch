package com.batch.Database.Repositories;

import com.batch.Database.Entities.BatchControllerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface BatchControllerDataRepository extends JpaRepository<BatchControllerData, Long> {

    @Modifying
    @Query(value = "Update BatchControllerData SET LockGeneralControl = ?1 WHERE Unit like ?2", nativeQuery = true)
    void updateLockGeneralControl(boolean b, String unitName);

    @Modifying
    @Query(value = "Update BatchControllerData SET LockGeneralControl = ?1, ControlBit = ?2  WHERE Unit like ?3", nativeQuery = true)
    void updateForBatchController(int currentParallelStepsNo, boolean controlBit, String unit);

    @Query(value = "SELECT * FROM BatchControllerData where Unit like ?1 ", nativeQuery = true)
    Optional<BatchControllerData> findByUnitName(String unitName);
}


