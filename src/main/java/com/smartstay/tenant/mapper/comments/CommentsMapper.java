package com.smartstay.tenant.mapper.comments;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.comment.CommentsListResponse;
import com.smartstay.tenant.dto.comment.ComplaintCommentProjection;

import java.util.function.Function;

public class CommentsMapper implements Function<ComplaintCommentProjection, CommentsListResponse> {
    @Override
    public CommentsListResponse apply(ComplaintCommentProjection complaintCommentProjection) {

        if (complaintCommentProjection != null) {
            CommentsListResponse response = new CommentsListResponse();
            response.setCommentId(complaintCommentProjection.getCommentId());
            response.setComment(complaintCommentProjection.getComment());
            response.setUserName(complaintCommentProjection.getUserName());
            response.setCommentDate(complaintCommentProjection.getCommentDate());
            response.setProfileUrl(complaintCommentProjection.getProfileUrl());
            if (complaintCommentProjection.getUserName() != null && !complaintCommentProjection.getUserName().isEmpty()) {
                response.setInitials(Utils.getInitials(complaintCommentProjection.getUserName()));
            } else {
                response.setInitials("");
            }
            return response;
        }
        return null;
    }
}
