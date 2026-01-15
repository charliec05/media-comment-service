package com.charliec.commentservice.service;

import com.charliec.commentservice.api.request.CreateCommentRequest;
import com.charliec.commentservice.domain.entity.Comment;
import com.charliec.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final byte STATUS_ACTIVE = 0;

    private final CommentRepository commentRepository;

    @Transactional
    public Comment createComment(String contentType, Long contentId, CreateCommentRequest req) {
        Long parentId = req.parentId();

        // 1) 先创建 comment（rootId 可能需要二次更新）
        Comment comment = Comment.builder()
                .contentType(contentType)
                .contentId(contentId)
                .userId(req.userId())
                .parentId(parentId)
                .body(req.body())
                .status(STATUS_ACTIVE)
                .build();

        // 2) 二级评论：校验 parent，设置 rootId = parent.rootId（或 parent.id）
        if (parentId != null) {
            Comment parent = commentRepository.findByIdAndStatusForUpdate(parentId, STATUS_ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found or inactive"));

            // 同一内容下才能回复（非常重要）
            if (!parent.getContentType().equals(contentType) || !parent.getContentId().equals(contentId)) {
                throw new IllegalArgumentException("Parent comment does not belong to the same content");
            }

            Long rootId = (parent.getRootId() != null) ? parent.getRootId() : parent.getId();
            comment.setRootId(rootId);

            Comment saved = commentRepository.save(comment);

            // 对 root 的 replyCount + 1（可选：也对 parent +1）
            commentRepository.incrementReplyCount(rootId);

            return saved;
        }

        // 3) 一级评论：先保存拿到 id，再把 rootId 设置为自己的 id
        Comment saved = commentRepository.save(comment);
        saved.setRootId(saved.getId());
        // 保存 rootId 更新（仍在同一事务里）
        return commentRepository.save(saved);
    }
}
