package com.smartstay.tenant.service;

import com.smartstay.tenant.dao.CustomersBedHistory;
import com.smartstay.tenant.dto.invoice.BedHistory;
import com.smartstay.tenant.mapper.bed.BedHistoryMapper;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.repository.CustomerBedHistoryRepository;
import com.smartstay.tenant.repository.FloorRepository;
import com.smartstay.tenant.repository.RoomRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerBedHistoryService {


    @Autowired
    private CustomerBedHistoryRepository customerBedHistoryRepository;

    private  BedHistoryMapper bedHistoryMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BedsRepository bedsRepository;


    public CustomersBedHistory getCustomerBedByStartDate(String customerId, Date startDate, Date endDate) {
        CustomersBedHistory cbh = customerBedHistoryRepository.findByCustomerIdAndDate(customerId, startDate, endDate);
        return cbh;
    }

    public CustomersBedHistory getCustomerBookedBed(String customerId) {
        return customerBedHistoryRepository.findByCustomerIdAndTypeBooking(customerId);
    }

    public List<CustomersBedHistory> getCustomerBedByDates(String customerId, Date startDate, Date endDate) {
        return customerBedHistoryRepository.findByDates(customerId, startDate, endDate);
    }

    public CustomersBedHistory getLatestRentAmount(String customerId) {
        return customerBedHistoryRepository.getLatestRentAmount(customerId, null);
    }

    public List<BedHistory> getBedHistory(String customerId, String hostelId, Date startDate, Date endDate) {
        bedHistoryMapper = new BedHistoryMapper(
                roomRepository,
                floorRepository,
                bedsRepository
        );
        return customerBedHistoryRepository
                .findBedHistoryInRange(customerId, hostelId, startDate, endDate)
                .stream()
                .map(bedHistoryMapper::map)
                .toList();
    }
}
