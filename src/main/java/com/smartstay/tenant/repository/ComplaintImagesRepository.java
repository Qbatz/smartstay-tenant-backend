package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.ComplaintImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintImagesRepository extends JpaRepository<ComplaintImages, Integer> {

    @Query(value = """
                SELECT * FROM complaint_images 
                WHERE id = :imageId 
                  AND complaint_id = :complaintId
                  AND is_deleted = false
            """, nativeQuery = true)
    ComplaintImages findByIdAndComplaintIdAndIsDeletedFalse(@Param("imageId") Integer imageId, @Param("complaintId") Integer complaintId);

}
