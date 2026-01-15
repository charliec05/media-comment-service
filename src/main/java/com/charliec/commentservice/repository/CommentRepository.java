package com.charliec.commentservice.repository;

import com.charliec.commentservice.domain.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndStatus(Long id, Integer status);

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
