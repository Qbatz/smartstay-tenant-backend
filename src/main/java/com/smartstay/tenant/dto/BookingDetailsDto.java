package com.smartstay.tenant.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record BookingDetailsDto(
        Integer bedId,
        Integer roomId,
        Integer floorId,
        Double rentAmount,
        Double bookingAmount,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date checkoutDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date requestedCheckoutDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date leavingDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date noticeDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date joiningDate,

        @JsonFormat(pattern = "dd/MM/yyyy")
        Date expectedJoiningDate,

        String bookingId,
        String currentStatus,
        String reasonForLeaving,

        String roomName,
        String floorName,
        String bedName
) {
}
