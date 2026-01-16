package com.charliec.commentservice.api;

import com.charliec.commentservice.api.request.CreateCommentRequest;
import com.charliec.commentservice.api.response.CommentResponse;
import com.charliec.commentservice.domain.entity.Comment;
import com.charliec.commentservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/contents/{contentType}/{contentId}/comments")
    public CommentResponse create(
            @PathVariable String contentType,
            @PathVariable Long contentId,
            @Valid @RequestBody CreateCommentRequest req
    ) {
        Comment c = commentService.createComment(contentType, contentId, req);

        return new CommentResponse(
                c.getId(),
                c.getContentType(),
                c.getContentId(),
                c.getUserId(),
                c.getParentId(),
                c.getRootId(),
                c.getBody(),
                c.getStatus(),
                c.getLikeCount(),
                c.getReplyCount(),
                c.getScore(),
                c.getCreatedAt()
        );
    }
}
