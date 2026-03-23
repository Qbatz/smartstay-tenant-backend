package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.BookingsV1;
import com.smartstay.tenant.repository.BookingsRepository;
import com.smartstay.tenant.response.customer.CustomersBookingDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingsService {

    @Autowired
    private BookingsRepository bookingsRepository;

    public CustomersBookingDetails getCustomerBookingDetails(String customerId) {
        return bookingsRepository.getCustomerBookingDetails(customerId);
    }

    public BookingsV1 getLatestBooking(String customerId, String hostelId) {
        return bookingsRepository.findTopByCustomerIdAndHostelIdOrderByJoiningDateDesc(customerId, hostelId);
    }
}
