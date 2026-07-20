package com.smartstay.tenant.mapper.hostel;

import com.smartstay.tenant.Utils.DateUtils;
import com.smartstay.tenant.Utils.FileUtils;
import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.*;
import com.smartstay.tenant.dto.files.FileDetails;
import com.smartstay.tenant.dto.hostel.HostelWithRentDTO;
import com.smartstay.tenant.dto.hostel.RentalDetailsDTO;
import com.smartstay.tenant.ennum.CustomerStatus;
import com.smartstay.tenant.response.customer.CustomerHostelDocsRes;
import com.smartstay.tenant.response.customer.CustomerHostels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HostelDetailsMapper implements Function<CustomerHostels, HostelWithRentDTO> {

    private final HostelV1 hostel;
    private final Customers customer;
    private final Users owner;
    private final BillingRules latestBillingRules;
    private final List<CustomerDocuments> customerDocuments;
    private final CustomersBedHistory latestBedHistory;
    private final Map<Integer, Beds> bedsMap;
    private final Map<Integer, Rooms> roomsMap;
    private final Map<Integer, Floors> floorsMap;
    private final BookingsV1 booking;
    private final InvoicesV1 bookingInvoice;
    private final InvoicesV1 advanceInvoice;

    public HostelDetailsMapper(HostelV1 hostel,
                               Customers customer,
                               Users owner,
                               BillingRules latestBillingRules,
                               List<CustomerDocuments> customerDocuments,
                               CustomersBedHistory latestBedHistory,
                               Map<Integer, Beds> bedsMap,
                               Map<Integer, Rooms> roomsMap,
                               Map<Integer, Floors> floorsMap,
                               BookingsV1 booking,
                               InvoicesV1 bookingInvoice,
                               InvoicesV1 advanceInvoice) {
        this.hostel = hostel;
        this.customer = customer;
        this.owner = owner;
        this.latestBillingRules = latestBillingRules;
        this.customerDocuments = customerDocuments;
        this.latestBedHistory = latestBedHistory;
        this.bedsMap = bedsMap;
        this.roomsMap = roomsMap;
        this.floorsMap = floorsMap;
        this.booking = booking;
        this.bookingInvoice = bookingInvoice;
        this.advanceInvoice = advanceInvoice;
    }

    @Override
    public HostelWithRentDTO apply(CustomerHostels customerHostels) {

        RentalDetailsDTO rentalDetailsDTO = getRentDetails();

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

    public RentalDetailsDTO getRentDetails() {

        RentalDetailsDTO rentalDetailsDTO = new RentalDetailsDTO();

        Integer bedId = null;
        String bedName = null;
        Integer roomId = null;
        String roomName = null;
        Integer floorId = null;
        String floorName = null;
        String joiningDate = null;
        String checkoutDate = null;
        String displayDuration = null;
        String reasonForLeaving = null;
        double rentAmount = 0;
        double bookingPaidAmount = 0;
        double bookingRefundedAmount = 0;
        double advancePaidAmount = 0;
        double advanceRefundedAmount = 0;
        String dueDateText = null;

        if (latestBedHistory != null) {
            bedId = latestBedHistory.getBedId();
            Beds bed = bedsMap.getOrDefault(bedId, null);
            if (bed != null) {
                bedName = bed.getBedName();
                roomId = bed.getRoomId();
                Rooms room = roomsMap.getOrDefault(roomId, null);
                if (room != null) {
                    roomName = room.getRoomName();
                    floorId = room.getFloorId();
                    Floors floor = floorsMap.getOrDefault(floorId, null);
                    if (floor != null) {
                        floorName = floor.getFloorName();
                    }
                }
            }
        }

        if (booking != null){
            Date dbJoiningDate = booking.getJoiningDate();
            Date dbCheckoutDate = booking.getCheckoutDate();
            if (dbJoiningDate != null) {
                joiningDate = Utils.dateToString(dbJoiningDate);
            }
            if (dbCheckoutDate != null) {
                checkoutDate = Utils.dateToString(dbCheckoutDate);
            }
            if (dbJoiningDate != null && dbCheckoutDate != null) {
                displayDuration = DateUtils.getDuration(dbJoiningDate, dbCheckoutDate);
            }
            reasonForLeaving = booking.getReasonForLeaving();
            rentAmount = booking.getRentAmount() != null ? booking.getRentAmount() : 0;
        }

        if (latestBillingRules != null) {
            Integer dueDayValue = latestBillingRules.getBillDueDays();

            if (dueDayValue != null && dueDayValue > 0) {
                dueDateText = dueDayValue + "th of every month";
            } else {
                dueDateText = "1th of every month";
            }
        }

        if (bookingInvoice != null){
            bookingPaidAmount = bookingInvoice.getPaidAmount() != null ? bookingInvoice.getPaidAmount() : 0;
            double bookingBalanceAmount = bookingInvoice.getBalanceAmount() != null ? bookingInvoice.getBalanceAmount() : 0;
            bookingRefundedAmount = bookingPaidAmount - bookingBalanceAmount;
            bookingPaidAmount = Utils.roundOffWithTwoDigit(bookingPaidAmount);
            bookingRefundedAmount = Utils.roundOffWithTwoDigit(bookingRefundedAmount);
        }

        if (advanceInvoice != null){
            advancePaidAmount = advanceInvoice.getPaidAmount() != null ? advanceInvoice.getPaidAmount() : 0;
            double advanceBalanceAmount = advanceInvoice.getBalanceAmount() != null ? advanceInvoice.getBalanceAmount() : 0;
            advanceRefundedAmount = advancePaidAmount - advanceBalanceAmount;
            advancePaidAmount = Utils.roundOffWithTwoDigit(advancePaidAmount);
            advanceRefundedAmount = Utils.roundOffWithTwoDigit(advanceRefundedAmount);
        }

        rentalDetailsDTO.setBedId(bedId);
        rentalDetailsDTO.setBedName(bedName);
        rentalDetailsDTO.setRoomId(roomId);
        rentalDetailsDTO.setRoomName(roomName);
        rentalDetailsDTO.setFloorId(floorId);
        rentalDetailsDTO.setFloorName(floorName);
        rentalDetailsDTO.setJoiningDate(joiningDate);
        rentalDetailsDTO.setCheckoutDate(checkoutDate);
        rentalDetailsDTO.setDisplayDuration(displayDuration);
        rentalDetailsDTO.setCheckOutReason(reasonForLeaving);
        rentalDetailsDTO.setRentAmount(rentAmount);
        rentalDetailsDTO.setBookingPaidAmount(bookingPaidAmount);
        rentalDetailsDTO.setBookingRefundedAmount(bookingRefundedAmount);
        rentalDetailsDTO.setAdvancePaidAmount(advancePaidAmount);
        rentalDetailsDTO.setAdvanceRefundedAmount(advanceRefundedAmount);
        rentalDetailsDTO.setDueDate(dueDateText);

        return rentalDetailsDTO;
    }
}
