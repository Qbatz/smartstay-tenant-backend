package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomerActivities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerActivitiesRepository extends JpaRepository<CustomerActivities, Long> {
}
