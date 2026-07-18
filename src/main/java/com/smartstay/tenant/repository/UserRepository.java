package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<Users, String> {

    Users findUserByUserId(String userId);

    List<Users> findByUserIdInAndRoleIdIn(List<String> userIds, List<Integer> roleIds);

    List<Users> findByUserIdIn(List<String> userIds);

    List<Users> findAllByParentIdInAndRoleIdAndIsActiveTrueAndIsDeletedFalse(Set<String> parentIds,
                                                                             int ownerRoleId);
}
