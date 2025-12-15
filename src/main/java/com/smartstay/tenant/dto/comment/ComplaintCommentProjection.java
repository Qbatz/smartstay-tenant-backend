package com.smartstay.tenant.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartstay.tenant.Utils.Utils;

import java.util.Date;

public interface ComplaintCommentProjection {

    Integer getCommentId();
    String getComment();
    String getUserName();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date getCommentDate();
    String getProfileUrl();

    default String getInitial() {
        return Utils.getInitials(getUserName());
    }
}
