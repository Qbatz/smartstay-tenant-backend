package com.smartstay.tenant.response.customer;

public record AdditionalContacts(Long contactId,
                                 String name,
                                 String relationship,
                                 String mobile,
                                 String occupation) {
}
