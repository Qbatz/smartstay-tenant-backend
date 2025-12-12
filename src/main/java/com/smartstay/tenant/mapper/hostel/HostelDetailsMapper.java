package com.smartstay.tenant.mapper.hostel;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.BillingRules;
import com.smartstay.tenant.dao.BookingsV1;
import com.smartstay.tenant.dto.hostel.HostelWithRentDTO;
import com.smartstay.tenant.dto.hostel.RentalDetailsDTO;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.customer.CustomerHostels;
import com.smartstay.tenant.service.BookingsService;
import com.smartstay.tenant.service.HostelConfigService;

import java.util.Date;
import java.util.function.Function;

public class HostelDetailsMapper implements Function<CustomerHostels, HostelWithRentDTO> {

    private final BookingsService bookingsService;
    private final HostelConfigService hostelConfigService;
    private final InvoicesV1Repository invoicesV1Repository;

    public HostelDetailsMapper(BookingsService bookingsService, HostelConfigService hostelConfigService, InvoicesV1Repository invoicesV1Repository) {
        this.bookingsService = bookingsService;
        this.hostelConfigService = hostelConfigService;
        this.invoicesV1Repository = invoicesV1Repository;
    }

    @Override
    public HostelWithRentDTO apply(CustomerHostels customerHostels) {

        RentalDetailsDTO rentalDetailsDTO = getRentDetails(customerHostels.getHostelId(), customerHostels.getCustomerId());

        return new HostelWithRentDTO(customerHostels.getCustomerId(), customerHostels.getHostelId(), customerHostels.getHostelName(), customerHostels.getHostelInitial(), customerHostels.getHouseNo(), customerHostels.getStreet(), customerHostels.getLandmark(), customerHostels.getPincode(), customerHostels.getCity(), customerHostels.getState(), customerHostels.getHostelPic(), customerHostels.getCurrentStatus(), customerHostels.getStatusCode(), rentalDetailsDTO);
    }

    public RentalDetailsDTO getRentDetails(String hostelId, String customerId) {

        RentalDetailsDTO rentalDetailsDTO = new RentalDetailsDTO();

        BookingsV1 bookingDetails = bookingsService.getLatestBooking(customerId, hostelId);

        if (bookingDetails == null) {
            rentalDetailsDTO.setJoiningDate(null);
            rentalDetailsDTO.setRentAmount(0.0);
            rentalDetailsDTO.setAdvancePaidAmount(0.0);
            rentalDetailsDTO.setDueDate("0th of every month");
            return rentalDetailsDTO;
        }

        // Safe values
        Double rentAmount = bookingDetails.getRentAmount() != null ? bookingDetails.getRentAmount() : 0.0;

        Double advancePaidAmount = invoicesV1Repository.findAdvancePaidAmount(customerId);
        if (advancePaidAmount == null) {
            advancePaidAmount = 0.0;
        }

        BillingRules billingRules =
                hostelConfigService.getLatestBillRuleByHostelIdAndStartDate(hostelId, new Date());

        int dueDay = (billingRules != null ? billingRules.getBillingDueDate() : 0);
        String dueDateText = (dueDay > 0 ? dueDay : "0") + "th of every month";

        rentalDetailsDTO.setJoiningDate(
                bookingDetails.getJoiningDate() != null
                        ? Utils.dateToString(bookingDetails.getJoiningDate())
                        : null
        );
        rentalDetailsDTO.setRentAmount(rentAmount);
        rentalDetailsDTO.setAdvancePaidAmount(advancePaidAmount);
        rentalDetailsDTO.setDueDate(dueDateText);

        return rentalDetailsDTO;
    }

}
