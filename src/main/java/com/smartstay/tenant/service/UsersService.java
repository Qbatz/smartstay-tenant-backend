package com.smartstay.tenant.service;

import com.smartstay.tenant.Utils.Constants;
import com.smartstay.tenant.dao.Users;
import com.smartstay.tenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository;

    public List<Users> getOwnersByParentIds(Set<String> parentIds) {

        int ownerRoleId = Constants.OWNER_ROLE_ID;

        return userRepository
                .findAllByParentIdInAndRoleIdAndIsActiveTrueAndIsDeletedFalse(parentIds, ownerRoleId);
    }
}
