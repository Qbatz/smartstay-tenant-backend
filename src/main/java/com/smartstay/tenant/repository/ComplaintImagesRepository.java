package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.ComplaintImages;
import com.smartstay.tenant.response.complaints.ComplaintImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintImagesRepository extends JpaRepository<ComplaintImages, Integer> {

    @Query(value = """
                SELECT * FROM complaint_images 
                WHERE id = :imageId 
                  AND complaint_id = :complaintId
                  AND is_deleted = false
            """, nativeQuery = true)
    ComplaintImages findByIdAndComplaintIdAndIsDeletedFalse(@Param("imageId") Integer imageId, @Param("complaintId") Integer complaintId);

    @Query("""
                SELECT new com.smartstay.tenant.response.complaints.ComplaintImage(
                    i.id,
                    i.imageUrl
                )
                FROM ComplaintImages i
                WHERE i.complaints.complaintId = :complaintId
                  AND i.isActive = true
                  AND i.isDeleted = false
                ORDER BY i.createdAt DESC
            """)
    List<ComplaintImage> findImagesByComplaintId(@Param("complaintId") Integer complaintId);
}

