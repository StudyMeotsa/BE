package com.example.growingstudy.groupsub.dto;

import com.example.growingstudy.groupsub.entity.GroupNotice;

import java.time.LocalDateTime;

public record CurrentNoticeResponse(
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static CurrentNoticeResponse from(GroupNotice groupNotice) {
        return new CurrentNoticeResponse(
                groupNotice.getTitle(),
                groupNotice.getContent(),
                groupNotice.getCreatedAt()
        );
    }
}
