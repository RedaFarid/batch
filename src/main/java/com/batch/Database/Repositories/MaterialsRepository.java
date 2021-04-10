package com.batch.Database.Repositories;


import com.batch.Database.Entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialsRepository extends JpaRepository<Material, Long> {

    Material findByName(String name);
}
