package com.smartstay.tenant.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public interface ComplaintCommentProjection {
    Integer getCommentId();
    String getComment();
    String getUserName();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date getCommentDate();
    String getProfileUrl();
}
