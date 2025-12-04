package com.smartstay.tenant.response.hostel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


public record RequestItemResponse (Long requestId,
                                   String type,
                                   String requestedDate,
                                   String status,
                                   String title,
                                   String description,
                                   String requestedItem,
                                   int statusCode){}

