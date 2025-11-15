package com.smartstay.tenant.response.complaints;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record ComplaintComment(
        Integer commentId,
        String comment,
        String userName,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Asia/Kolkata")
        Date commentDate
) {}