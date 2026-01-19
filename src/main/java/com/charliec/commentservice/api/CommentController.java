package com.charliec.commentservice.api;

import com.charliec.commentservice.api.request.CreateCommentRequest;
import com.charliec.commentservice.api.response.CommentResponse;
import com.charliec.commentservice.domain.entity.Comment;
import com.charliec.commentservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        return toResp(c);
    }

    /**
     * 一级评论分页（parentId is null）
     * sort=new: createdAt desc
     * sort=hot: score desc, id desc
     */
    @GetMapping("/contents/{contentType}/{contentId}/comments")
    public Page<CommentResponse> listRootComments(
            @PathVariable String contentType,
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "new") String sort
    ) {
        Sort s = "hot".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.DESC, "id"))
                : Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"));

        PageRequest pr = PageRequest.of(page, size, s);
        return commentService.listRootComments(contentType, contentId, pr).map(this::toResp);
    }

    /**
     * 二级评论分页（rootId = 一级评论 id；查其直接子评论：parentId = rootId）
     * sort=new: createdAt asc（更符合阅读顺序）
     * sort=hot: score desc, id desc
     */
    @GetMapping("/comments/{rootId}/replies")
    public Page<CommentResponse> listReplies(
            @PathVariable Long rootId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "new") String sort
    ) {
        Sort s = "hot".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "score").and(Sort.by(Sort.Direction.DESC, "id"))
                : Sort.by(Sort.Direction.ASC, "createdAt").and(Sort.by(Sort.Direction.ASC, "id"));

        PageRequest pr = PageRequest.of(page, size, s);
        return commentService.listReplies(rootId, pr).map(this::toResp);
    }

    private CommentResponse toResp(Comment c) {
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
