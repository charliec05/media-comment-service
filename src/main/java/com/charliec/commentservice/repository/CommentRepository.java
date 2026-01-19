package com.charliec.commentservice.repository;

import com.charliec.commentservice.domain.entity.Comment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndStatus(Long id, Byte status);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select c from Comment c where c.id = :id and c.status = :status")
    Optional<Comment> findByIdAndStatusForUpdate(@Param("id") Long id, @Param("status") Byte status);

    @Modifying
    @Query("update Comment c set c.replyCount = c.replyCount + 1 where c.id = :id")
    int incrementReplyCount(@Param("id") Long id);

    // 一级评论分页（parent_id is null）
    Page<Comment> findByContentTypeAndContentIdAndParentIdIsNullAndStatus(
            String contentType,
            Long contentId,
            Byte status,
            Pageable pageable
    );

    // 二级评论分页（某个一级评论的直接回复：parent_id = rootId）
    Page<Comment> findByParentIdAndStatus(
            Long parentId,
            Byte status,
            Pageable pageable
    );
}
