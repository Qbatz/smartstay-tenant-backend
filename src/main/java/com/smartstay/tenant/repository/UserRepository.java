package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, String> {

    Users findUserByUserId(String userId);
}
