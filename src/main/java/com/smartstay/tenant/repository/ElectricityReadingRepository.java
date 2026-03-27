package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.ElectricityReadings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricityReadingRepository extends JpaRepository<ElectricityReadings, Integer> {
}
