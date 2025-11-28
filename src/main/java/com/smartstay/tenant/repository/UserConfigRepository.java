package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfigRepository extends JpaRepository<UserConfig, String> {

    UserConfig findByUserId(String userId);
}
