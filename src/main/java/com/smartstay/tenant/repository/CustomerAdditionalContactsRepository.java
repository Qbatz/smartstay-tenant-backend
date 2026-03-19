package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.CustomerAdditionalContacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CustomerAdditionalContactsRepository extends JpaRepository<CustomerAdditionalContacts, Long> {

    List<CustomerAdditionalContacts> findAllByCustomerIdAndIsDeletedFalseOrderByCreatedAtDesc(String customerId);

    List<CustomerAdditionalContacts> findAllByContactIdInAndCustomerIdAndIsDeletedFalse(Set<Long> contactIds, String customerId);

    List<CustomerAdditionalContacts> findAllByCustomerIdAndContactIdInAndIsDeletedFalseOrderByCreatedAtDesc(String customerId, Set<Long> contactIds);
}
