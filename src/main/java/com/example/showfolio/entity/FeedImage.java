package com.example.showfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feed_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FeedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "order_num", nullable = false)
    private int orderNum;
}
