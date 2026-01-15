package com.charliec.commentservice.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotNull Long userId,

        // 二级评论时必填；一级评论传 null
        Long parentId,

        @NotBlank
        @Size(max = 5000)
        String body
) {}
