package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.ComplaintUpdates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ComplaintUpdatesRepository extends JpaRepository<ComplaintUpdates, Long> {
}

