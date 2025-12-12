package com.smartstay.tenant.service;

import com.smartstay.tenant.config.Authentication;
import com.smartstay.tenant.dao.BookingsV1;
import com.smartstay.tenant.repository.BookingsRepository;
import com.smartstay.tenant.response.customer.CustomersBookingDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BookingsService {

    @Autowired
    private BookingsRepository bookingsRepository;


    @Autowired
    private Authentication authentication;


    public CustomersBookingDetails getCustomerBookingDetails(String customerId) {
        return bookingsRepository.getCustomerBookingDetails(customerId);
    }

    public BookingsV1 getLatestBooking(String customerId, String hostelId) {
        return bookingsRepository.findTopByCustomerIdAndHostelIdOrderByJoiningDateDesc(customerId, hostelId);
    }

}
