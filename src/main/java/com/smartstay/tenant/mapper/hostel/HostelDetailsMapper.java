package com.smartstay.tenant.mapper.hostel;

import com.smartstay.tenant.Utils.FileUtils;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.files.FileDetails;
import com.smartstay.tenant.dto.hostel.HostelWithRentDTO;
import com.smartstay.tenant.dto.hostel.RentalDetailsDTO;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.repository.InvoicesV1Repository;
import com.smartstay.tenant.response.customer.CustomerHostelDocsRes;
import com.smartstay.tenant.response.customer.CustomerHostels;
import com.smartstay.tenant.service.BookingsService;
import com.smartstay.tenant.service.HostelConfigService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class HostelDetailsMapper implements Function<CustomerHostels, HostelWithRentDTO> {

    private final BookingsService bookingsService;
    private final HostelConfigService hostelConfigService;
    private final InvoicesV1Repository invoicesV1Repository;
    private final HostelV1 hostel;
    private final Customers customer;
    private final Users owner;
    private final BillingRules latestBillingRules;
    private final List<CustomerDocuments> customerDocuments;

    public HostelDetailsMapper(BookingsService bookingsService,
                               HostelConfigService hostelConfigService,
                               InvoicesV1Repository invoicesV1Repository,
                               HostelV1 hostel,
                               Customers customer,
                               Users owner,
                               BillingRules latestBillingRules,
                               List<CustomerDocuments> customerDocuments) {
        this.bookingsService = bookingsService;
        this.hostelConfigService = hostelConfigService;
        this.invoicesV1Repository = invoicesV1Repository;
        this.hostel = hostel;
        this.customer = customer;
        this.owner = owner;
        this.latestBillingRules = latestBillingRules;
        this.customerDocuments = customerDocuments;
    }

    @Override
    public HostelWithRentDTO apply(CustomerHostels customerHostels) {

        RentalDetailsDTO rentalDetailsDTO = getRentDetails(customerHostels.getHostelId(),
                customerHostels.getCustomerId());

        String hostelInitial = null;
        if (customerHostels.getHostelName() != null &&
                !customerHostels.getHostelName().trim().equalsIgnoreCase("")) {
            hostelInitial = Utils.getInitials(customerHostels.getHostelName());
        }

        String hostelMobile = null;
        String hostelFullAddress = null;
        if (hostel != null) {
            hostelMobile = hostel.getMobile();
            hostelFullAddress = Utils.buildFullAddress(hostel);
        }

        String ownerId = null;
        String ownerName = null;
        if (owner != null){
            ownerId = owner.getUserId();
            ownerName = Utils.getFullName(owner.getFirstName(), owner.getLastName());
        }

        boolean canRaiseNotice = false;
        int noticeDays = 0;
        if (customer != null){
            if (CustomerStatus.ACTIVE.name().equals(customer.getCurrentStatus()) ||
                    CustomerStatus.CHECK_IN.name().equals(customer.getCurrentStatus()) ||
                    CustomerStatus.WALKED_IN.name().equals(customer.getCurrentStatus())){
                canRaiseNotice = true;
            }
        }
        if (latestBillingRules != null){
            noticeDays = latestBillingRules.getNoticePeriod() != null ? latestBillingRules.getNoticePeriod() : 0;
        }

        List<CustomerHostelDocsRes> customerHostelDocsRes = new ArrayList<>();
        if (customerDocuments != null && !customerDocuments.isEmpty()) {
            for (CustomerDocuments document : customerDocuments) {

                FileDetails fileDetails = FileUtils.getFileDetailsFromUrl(document.getDocumentUrl());

                String docFileExtType = null;
                String docFileName = null;
                String docFileSize = null;

                if (fileDetails != null) {
                    docFileExtType = fileDetails.extension();
                    docFileName = fileDetails.fileName();
                    long fileSizeInBytes = fileDetails.sizeInBytes() != null ? fileDetails.sizeInBytes() : 0;
                    docFileSize = FileUtils.formatFileSize(fileSizeInBytes);
                }

                CustomerHostelDocsRes hostelDocsRes = new CustomerHostelDocsRes(document.getDocumentId(),
                        document.getDocumentType(), document.getDocumentFileType(), docFileExtType,
                        docFileName, docFileSize, document.getDocumentUrl());

                customerHostelDocsRes.add(hostelDocsRes);
            }
        }

        return new HostelWithRentDTO(customerHostels.getCustomerId(), customerHostels.getHostelId(),
                customerHostels.getHostelName(), ownerId, ownerName, hostelInitial, customerHostels.getHostelPic(),
                hostelMobile, customerHostels.getHouseNo(), customerHostels.getStreet(), customerHostels.getLandmark(),
                customerHostels.getPincode(), customerHostels.getCity(), customerHostels.getState(),
                hostelFullAddress, customerHostels.getCurrentStatus(), customerHostels.getStatusCode(),
                canRaiseNotice, noticeDays, rentalDetailsDTO, customerHostelDocsRes);
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

        Integer dueDayValue = (billingRules != null) ? billingRules.getBillDueDays() : null;

        String dueDateText;
        if (dueDayValue != null && dueDayValue > 0) {
            dueDateText = dueDayValue + "th of every month";
        } else {
            dueDateText = "1th of every month";
        }

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
