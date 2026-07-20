package com.smartstay.tenant.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalDetailsDTO {
    private Integer bedId;
    private String bedName;
    private Integer roomId;
    private String roomName;
    private Integer floorId;
    private String floorName;
    private String joiningDate;
    private String checkoutDate;
    private String displayDuration;
    private String checkOutReason;
    private Double rentAmount;
    private Double bookingPaidAmount;
    private Double bookingRefundedAmount;
    private Double advancePaidAmount;
    private Double advanceRefundedAmount;
    private String dueDate;
}
