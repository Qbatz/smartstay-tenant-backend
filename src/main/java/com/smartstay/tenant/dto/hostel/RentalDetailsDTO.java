package com.smartstay.tenant.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalDetailsDTO {
    private String joiningDate;
    private Double rentAmount;
    private Double advancePaidAmount;
    private String dueDate;
}
