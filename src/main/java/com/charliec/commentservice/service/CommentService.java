package com.charliec.commentservice.service;

import com.charliec.commentservice.api.request.CreateCommentRequest;
import com.charliec.commentservice.domain.entity.Comment;
import com.charliec.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Comment comment = Comment.builder()
                .contentType(contentType)
                .contentId(contentId)
                .userId(req.userId())
                .parentId(parentId)
                .body(req.body())
                .status(STATUS_ACTIVE)
                .build();

        // 二级评论
        if (parentId != null) {
            Comment parent = commentRepository.findByIdAndStatusForUpdate(parentId, STATUS_ACTIVE)
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found or inactive"));

            if (!parent.getContentType().equals(contentType) || !parent.getContentId().equals(contentId)) {
                throw new IllegalArgumentException("Parent comment does not belong to the same content");
            }

            Long rootId = (parent.getRootId() != null) ? parent.getRootId() : parent.getId();
            comment.setRootId(rootId);

            Comment saved = commentRepository.save(comment);

            // root 总回复数 +1
            commentRepository.incrementReplyCount(rootId);

            // parent 直接回复数 +1（当 parent != root 时才需要）
            if (!parentId.equals(rootId)) {
                commentRepository.incrementReplyCount(parentId);
            }

            return saved;
        }

        // 一级评论：先保存拿 id，再把 rootId 设置为自己
        Comment saved = commentRepository.save(comment);
        saved.setRootId(saved.getId());
        return commentRepository.save(saved);
    }

    @Transactional(readOnly = true)
    public Page<Comment> listRootComments(String contentType, Long contentId, Pageable pageable) {
        return commentRepository.findByContentTypeAndContentIdAndParentIdIsNullAndStatus(
                contentType, contentId, STATUS_ACTIVE, pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<Comment> listReplies(Long rootId, Pageable pageable) {
        return commentRepository.findByParentIdAndStatus(rootId, STATUS_ACTIVE, pageable);
    }
}
