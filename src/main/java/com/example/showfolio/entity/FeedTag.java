package com.example.showfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feed_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"feed_id", "tag_name"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FeedTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(name = "tag_name", length = 50, nullable = false)
    private String tagName;
}
