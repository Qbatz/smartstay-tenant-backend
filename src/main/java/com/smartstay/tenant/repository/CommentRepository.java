package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments,Long> {


}
