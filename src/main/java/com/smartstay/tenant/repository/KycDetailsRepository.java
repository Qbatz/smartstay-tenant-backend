package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.KycDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface KycDetailsRepository extends JpaRepository<KycDetails, Long> {

    @Query("""
            select count(kd)
            from KycDetails kd
            where kd.hostelId = :hostelId
                and Date(kd.createdAt) >= Date(:afterDate)
            """)
    long findCountByHostelIdAndAfterDate(String hostelId, Date afterDate);
}
