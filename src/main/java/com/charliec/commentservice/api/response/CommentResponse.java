package com.charliec.commentservice.api.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String contentType,
        Long contentId,
        Long userId,
        Long parentId,
        Long rootId,
        String body,
        Byte status,
        Integer likeCount,
        Integer replyCount,
        Double score,
        LocalDateTime createdAt
) {}
