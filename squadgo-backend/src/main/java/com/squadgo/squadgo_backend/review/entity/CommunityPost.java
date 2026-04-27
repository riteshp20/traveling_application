package com.squadgo.squadgo_backend.review.entity;

import com.squadgo.squadgo_backend.auth.entity.User;
import com.squadgo.squadgo_backend.destination.entity.Destination;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "community_posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "destination_id", nullable = true)
    private Destination destination;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String caption;

    @Column(columnDefinition = "TEXT")
    private String mediaUrls;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Builder.Default
    @Column(nullable = false)
    private Integer likesCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
