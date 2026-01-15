package com.charliec.commentservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "comments",
       indexes = {
           @Index(name = "idx_content_parent_score",
                  columnList = "contentType, contentId, parentId, status, score, id"),
           @Index(name = "idx_content_parent_time",
                  columnList = "contentType, contentId, parentId, status, createdAt, id"),
           @Index(name = "idx_root_time",
                  columnList = "rootId, status, createdAt, id")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String contentType;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false)
    private Long userId;

    /** 一级评论为 null，二级评论指向被回复的 comment */
    private Long parentId;

    /** 一级评论 rootId = 自己的 id；二级评论 rootId = 一级评论 id */
    private Long rootId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /** 0 = 正常，1 = 删除，2 = 审核中 */
    @Builder.Default
    @JdbcTypeCode(Types.TINYINT)
    @Column(nullable = false)
    private Byte status = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer replyCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Double score = 0.0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
