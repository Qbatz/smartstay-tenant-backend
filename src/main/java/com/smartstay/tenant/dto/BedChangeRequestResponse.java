package com.smartstay.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BedChangeRequestResponse {

    private Long requestId;

    private Integer bedId;
    private String bedName;

    private Integer floorId;
    private String floorName;

    private Integer roomId;
    private String roomName;

    private Date startsFrom;
    private String reason;
    private String preferredType;
    private String currentStatus;
}

