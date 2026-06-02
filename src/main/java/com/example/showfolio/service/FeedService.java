package com.example.showfolio.service;

import com.example.showfolio.dto.request.FeedCreateRequest;
import com.example.showfolio.dto.request.FeedUpdateRequest;
import com.example.showfolio.dto.response.FeedLikeResponse;
import com.example.showfolio.dto.response.FeedResponse;
import com.example.showfolio.entity.*;
import com.example.showfolio.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedLikeRepository feedLikeRepository;
    private final FeedTagRepository feedTagRepository;
    private final FeedImageRepository feedImageRepository;
    private final FeedMentionRepository feedMentionRepository;
    private final UserRepository userRepository;


    // 피드 작성
    @Transactional
    public FeedResponse createFeed(FeedCreateRequest request, Long currentUserId) {

        // content랑 image 둘 다 없으면 예외
        if ((request.getContent() == null || request.getContent().isBlank())
                && request.getImageUrls().isEmpty()) {
            throw new IllegalArgumentException("내용 또는 이미지를 입력해주세요");
        }

        User member = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        Feed feed = Feed.builder()
                .user(member)
                .title(request.getTitle())
                .content(request.getContent())
                .visibility(Visibility.valueOf(request.getVisibility()))
                .projectId(request.getProjectId())
                .build();

        feedRepository.save(feed);

        request.getTags().stream()
                .distinct()
                .forEach(tagName -> {
                    FeedTag tag = FeedTag.builder()
                            .feed(feed)
                            .tagName(tagName)
                            .build();
                    feedTagRepository.save(tag);
                });

        List<String> imageUrls = request.getImageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
            FeedImage image = FeedImage.builder()
                    .feed(feed)
                    .imageUrl(imageUrls.get(i))
                    .orderNum(i)
                    .build();
            feedImageRepository.save(image);
        }

        request.getMentionedUserIds().forEach(mentionedUserId -> {
            User mentionedMember = userRepository.findById(mentionedUserId)
                    .orElseThrow(() -> new EntityNotFoundException("멘션한 유저를 찾을 수 없습니다"));

            FeedMention mention = FeedMention.builder()
                    .feed(feed)
                    .mentionedUser(mentionedMember)
                    .build();
            feedMentionRepository.save(mention);
        });

        return FeedResponse.from(feed, false);
    }

    // 피드 단건 조회
    public FeedResponse getFeed(Long feedId, Long currentUserId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        validateVisibility(feed, currentUserId);

        boolean isLiked = feedLikeRepository
                .existsByFeedIdAndUserId(feedId, currentUserId);

        return FeedResponse.from(feed, isLiked);
    }

    // 피드 수정
    @Transactional
    public FeedResponse updateFeed(Long feedId,
                                   FeedUpdateRequest request,
                                   Long currentUserId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        if (!feed.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        feed.update(
                request.getTitle(),
                request.getContent(),
                Visibility.valueOf(request.getVisibility()),
                request.getProjectId()
        );

        // 태그 초기화 후 재저장
        feedTagRepository.deleteByFeedId(feedId);
        request.getTags().stream()
                .distinct()
                .forEach(tagName -> {
                    FeedTag tag = FeedTag.builder()
                            .feed(feed)
                            .tagName(tagName)
                            .build();
                    feedTagRepository.save(tag);
                });

        // 이미지 초기화 후 재저장
        feedImageRepository.deleteByFeedId(feedId);
        List<String> imageUrls = request.getImageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
            FeedImage image = FeedImage.builder()
                    .feed(feed)
                    .imageUrl(imageUrls.get(i))
                    .orderNum(i)
                    .build();
            feedImageRepository.save(image);
        }

        // 멘션 초기화 후 재저장
        feedMentionRepository.deleteByFeedId(feedId);
        request.getMentionedUserIds().forEach(mentionedUserId -> {
            User mentionedMember = userRepository.findById(mentionedUserId)
                    .orElseThrow(() -> new EntityNotFoundException("멘션한 유저를 찾을 수 없습니다"));

            FeedMention mention = FeedMention.builder()
                    .feed(feed)
                    .mentionedUser(mentionedMember)
                    .build();
            feedMentionRepository.save(mention);
        });

        boolean isLiked = feedLikeRepository
                .existsByFeedIdAndUserId(feedId, currentUserId);

        return FeedResponse.from(feed, isLiked);
    }

    // 피드 삭제
    @Transactional
    public void deleteFeed(Long feedId, Long currentUserId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        if (!feed.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        feedRepository.delete(feed);
    }


    // 피드 목록 조회

    // 전체 피드 (최신순)
    public Page<FeedResponse> getFeeds(int page, Long currentUserId) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Feed> feeds = feedRepository.findPublicFeeds(pageable);

        return feeds.map(feed -> {
            boolean isLiked = feedLikeRepository
                    .existsByFeedIdAndUserId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 팔로잉 피드 (수정 필요)
    public Page<FeedResponse> getFollowingFeeds(
            List<Long> followingIds,
            int page,
            Long currentUserId) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Feed> feeds = feedRepository
                .findFollowingFeeds(followingIds, pageable);

        return feeds.map(feed -> {
            boolean isLiked = feedLikeRepository
                    .existsByFeedIdAndUserId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 내 피드
    public Page<FeedResponse> getMyFeeds(int page, Long currentUserId) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Feed> feeds = feedRepository.findMyFeeds(currentUserId, pageable);

        return feeds.map(feed -> {
            boolean isLiked = feedLikeRepository
                    .existsByFeedIdAndUserId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 좋아요
    @Transactional
    public FeedLikeResponse toggleLike(Long feedId, Long currentUserId) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        User member = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        boolean isLiked = feedLikeRepository
                .existsByFeedIdAndUserId(feedId, currentUserId);

        if (isLiked) {
            feedLikeRepository.deleteByFeedIdAndUserId(feedId, currentUserId);
        } else {
            FeedLike feedLike = FeedLike.builder()
                    .feed(feed)
                    .user(member)
                    .build();
            feedLikeRepository.save(feedLike);
        }

        int likeCount = feedLikeRepository.countByFeedId(feedId);

        return FeedLikeResponse.of(feedId, likeCount, !isLiked);
    }

    // 좋아요한 피드 목록
    public Page<FeedResponse> getLikedFeeds(
            int page,
            int size,
            Long currentUserId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Feed> feeds = feedLikeRepository
                .findLikedFeedsByUserId(currentUserId, pageable);

        return feeds.map(feed ->
                FeedResponse.from(feed, true)
        );
    }

    // 피드 검색
    public Page<FeedResponse> searchFeeds(
            String keyword,
            String tags,
            String author,
            String sort,
            int page,
            int size,
            Long currentUserId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Feed> feeds = feedRepository
                .searchFeeds(keyword, tags, author, sort, pageable);

        return feeds.map(feed -> {
            boolean isLiked = feedLikeRepository
                    .existsByFeedIdAndUserId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 공개 범위 검증
    private void validateVisibility(Feed feed, Long currentUserId) {
        switch (feed.getVisibility()) {
            case PRIVATE -> {
                if (!feed.getUser().getId().equals(currentUserId)) {
                    throw new IllegalArgumentException("비공개 피드입니다");
                }
            }
            case FOLLOWERS_ONLY -> {

            }
            case PUBLIC -> {

            }
        }
    }
}
