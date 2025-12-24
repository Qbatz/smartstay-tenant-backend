package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BillTemplates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillTemplatesRepository extends JpaRepository<BillTemplates, Integer> {

    BillTemplates getByHostelId(String hostelId);
}
