package com.charliec.commentservice.repository;

import com.charliec.commentservice.domain.entity.Comment;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndStatus(Long id, Integer status);
    Optional<Comment> findByIdAndStatus(Long id, Byte status);

    @Modifying
    @Query("update Comment c set c.replyCount = c.replyCount + 1 where c.id = :id")
    int incrementReplyCount(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select c from Comment c where c.id = :id and c.status = :status")
    Optional<Comment> findByIdAndStatusForUpdate(@Param("id") Long id, @Param("status") Byte status);


    List<Comment> findByContentTypeAndContentIdAndParentIdIsNullAndStatus(
            String contentType,
            Long contentId,
            Integer status,
            Pageable pageable
    );

    List<Comment> findByRootIdAndStatus(
            Long rootId,
            Integer status,
            Pageable pageable
    );
}
