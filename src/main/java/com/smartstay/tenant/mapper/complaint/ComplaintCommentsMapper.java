package com.smartstay.tenant.mapper.complaint;

import com.smartstay.tenant.dao.Customers;
import com.smartstay.tenant.dao.Users;
import com.smartstay.tenant.dto.complaint.ComplaintComments;

import java.util.List;
import java.util.function.Function;

public class ComplaintCommentsMapper implements Function<com.smartstay.tenant.dao.ComplaintComments, com.smartstay.tenant.dto.complaint.ComplaintComments> {

    List<Customers> listCustomers = null;
    List<Users> listUsers = null;

    public ComplaintCommentsMapper(List<Customers> listCustomers, List<Users> listUsers) {
        this.listCustomers = listCustomers;
        this.listUsers = listUsers;
    }

    @Override
    public ComplaintComments apply(com.smartstay.tenant.dao.ComplaintComments complaintComments) {
        return null;
    }
}
