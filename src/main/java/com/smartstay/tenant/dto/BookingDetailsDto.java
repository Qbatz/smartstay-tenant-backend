package com.smartstay.tenant.dto;

public record BookingDetailsDto(
        Integer bedId,
        Integer roomId,
        Integer floorId,
        Double rentAmount,
        Double bookingAmount,
        String checkoutDate,
        String requestedCheckoutDate,
        String leavingDate,
        String noticeDate,
        String joiningDate,
        String expectedJoiningDate,
        String bookingId,
        String currentStatus,
        String reasonForLeaving,
        String roomName,
        String floorName,
        String bedName,
        int roomSharingType
) {
}
