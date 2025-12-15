package com.smartstay.tenant.repository;


import com.smartstay.tenant.dao.ComplaintComments;
import com.smartstay.tenant.dto.comment.ComplaintCommentProjection;
import com.smartstay.tenant.response.complaints.ComplaintComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintCommentsRepository extends JpaRepository<ComplaintComments, String> {


    @Query("SELECT new com.smartstay.tenant.response.complaints.ComplaintComment(" + "c.commentId, c.comment, c.userName, c.commentDate) " + "FROM ComplaintComments c " + "WHERE c.complaint.complaintId = :complaintId " + "AND c.isActive = true ORDER BY c.createdAt ASC")
    List<ComplaintComment> findCommentsByComplaintId(@Param("complaintId") Integer complaintId);

    @Query(value = """
                SELECT
                    cc.comment_id AS commentId,
                    cc.comment AS comment,
                    cc.user_name AS userName,
                    cc.comment_date AS commentDate,
                    CASE
                        WHEN cc.user_type = 'TENANT'
                            THEN cust.profile_pic
                        ELSE NULL
                    END AS profileUrl
                FROM complaint_comments cc
                LEFT JOIN customers cust
                       ON cc.created_by = cust.customer_id
                WHERE cc.complaint_id = :complaintId
                  AND cc.is_active = TRUE
                ORDER BY cc.created_at ASC
            """, nativeQuery = true)
    List<ComplaintCommentProjection> findCommentsByComplaintIds(
            @Param("complaintId") Integer complaintId
    );

    List<ComplaintComments> findByComplaint_ComplaintIdAndIsActiveTrue(Integer complaintId);



}
