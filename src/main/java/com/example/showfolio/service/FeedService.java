package com.example.showfolio.service;

import com.example.showfolio.dto.request.FeedCreateRequest;
import com.example.showfolio.dto.request.FeedUpdateRequest;
import com.example.showfolio.dto.response.FeedLikeResponse;
import com.example.showfolio.dto.response.FeedResponse;
import com.example.showfolio.entity.*;
import com.example.showfolio.enums.Visibility;
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
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final FollowRepository followRepository;
    private final ProjectRepository projectRepository;


    // 피드 작성
    @Transactional
    public FeedResponse createFeed(FeedCreateRequest request, Long currentUserId) {

        // content랑 image 둘 다 없으면 예외
        if ((request.getContent() == null || request.getContent().isBlank())
                && request.getImageUrls().isEmpty()) {
            throw new IllegalArgumentException("내용 또는 이미지를 입력해주세요");
        }

        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        //프로젝트 검증
        Project project = null;
        if (request.getProjectId() != null) {
            // 삭제되지 않고 살아있는 프로젝트인지 조회
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(com.example.showfolio.exception.ProjectNotFoundException::new); // 팀원 예외 재활용!

            // 본인 소유의 프로젝트가 맞는지 체크
            if (!project.isOwnedBy(currentUserId)) { //
                throw new com.example.showfolio.exception.ProjectAccessDeniedException(); // 팀원 예외 재활용!
            }
        }


        Feed feed = Feed.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .visibility(request.getVisibility())
                .project(project)
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
            Member mentionedMember = memberRepository.findById(mentionedUserId)
                    .orElseThrow(() -> new EntityNotFoundException("멘션한 유저를 찾을 수 없습니다"));

            FeedMention mention = FeedMention.builder()
                    .feed(feed)
                    .mentionedUser(mentionedMember)
                    .build();
            feedMentionRepository.save(mention);

            // 멘션 알림 생성
            notificationService.createMentionNotification(
                    member,
                    mentionedMember,
                    feed
            );
        });

        return FeedResponse.from(feed, false);
    }

    // 피드 단건 조회
    public FeedResponse getFeed(Long feedId, Long currentUserId) {

        Feed feed = feedRepository.findByIdWithMember(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        validateVisibility(feed, currentUserId);

        boolean isLiked = feedLikeRepository
                .existsByFeedIdAndMemberId(feedId, currentUserId);

        int commentCount = feedRepository.countCommentsByFeedId(feedId);

        List<String> tags = feedTagRepository.findByFeedId(feedId)
                .stream()
                .map(t -> t.getTagName())
                .toList();

        List<String> imageUrls = feedImageRepository.findByFeedIdOrderByOrderNumAsc(feedId)
                .stream()
                .map(i -> i.getImageUrl())
                .toList();

        List<Long> mentionedUserIds = feedMentionRepository.findByFeedId(feedId)
                .stream()
                .map(m -> m.getMentionedUser().getId())
                .toList();

        return FeedResponse.from(feed, isLiked, commentCount, tags, imageUrls, mentionedUserIds);
    }

    // 피드 수정
    @Transactional
    public FeedResponse updateFeed(Long feedId,
                                   FeedUpdateRequest request,
                                   Long currentUserId) {

        Feed feed = feedRepository.findByIdWithMember(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        if (!feed.getMember().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        Project targetProject = null;
        if (request.getProjectId() != null) {
            targetProject = projectRepository.findById(request.getProjectId())
                    .orElseThrow(com.example.showfolio.exception.ProjectNotFoundException::new);

            if (!targetProject.isOwnedBy(currentUserId)) { //
                throw new com.example.showfolio.exception.ProjectAccessDeniedException();
            }
        }

        feed.update(
                request.getTitle(),
                request.getContent(),
                request.getVisibility(),
                targetProject
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
            Member mentionedMember = memberRepository.findById(mentionedUserId)
                    .orElseThrow(() -> new EntityNotFoundException("멘션한 유저를 찾을 수 없습니다"));

            FeedMention mention = FeedMention.builder()
                    .feed(feed)
                    .mentionedUser(mentionedMember)
                    .build();
            feedMentionRepository.save(mention);
        });

        boolean isLiked = feedLikeRepository
                .existsByFeedIdAndMemberId(feedId, currentUserId);

        int commentCount = feedRepository.countCommentsByFeedId(feedId);

        List<String> tags = feedTagRepository.findByFeedId(feedId)
                .stream()
                .map(t -> t.getTagName())
                .toList();

        List<String> savedImageUrls = feedImageRepository.findByFeedIdOrderByOrderNumAsc(feedId)
                .stream()
                .map(i -> i.getImageUrl())
                .toList();

        List<Long> mentionedUserIds = feedMentionRepository.findByFeedId(feedId)
                .stream()
                .map(m -> m.getMentionedUser().getId())
                .toList();

        return FeedResponse.from(feed, isLiked, commentCount, tags, savedImageUrls, mentionedUserIds);
    }

    // 피드 삭제
    @Transactional
    public void deleteFeed(Long feedId, Long currentUserId) {

        Feed feed = feedRepository.findByIdWithMember(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        if (!feed.getMember().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        feedImageRepository.deleteByFeedId(feedId);
        feedTagRepository.deleteByFeedId(feedId);
        feedMentionRepository.deleteByFeedId(feedId);
        feedLikeRepository.deleteByFeedId(feedId);

        feedRepository.delete(feed);
    }


    // 피드 목록 조회

    // 전체 피드 (최신순)
    public Page<FeedResponse> getFeeds(int page, Long currentUserId) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Feed> feeds = feedRepository.findPublicFeeds(pageable);

        return feeds.map(feed -> {
            boolean isLiked = feedLikeRepository
                    .existsByFeedIdAndMemberId(feed.getId(), currentUserId);
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
                    .existsByFeedIdAndMemberId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 내 피드
    public Page<FeedResponse> getMyFeeds(int page, Long currentUserId) {

        Pageable pageable = PageRequest.of(page, 20);
        Page<Feed> feeds = feedRepository.findMyFeeds(currentUserId, pageable);

        return feeds.map(feed -> {
            boolean isLiked = feedLikeRepository
                    .existsByFeedIdAndMemberId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 좋아요
    @Transactional
    public FeedLikeResponse toggleLike(Long feedId, Long currentUserId) {

        Feed feed = feedRepository.findByIdWithMember(feedId)
                .orElseThrow(() -> new EntityNotFoundException("피드를 찾을 수 없습니다"));

        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

        boolean isLiked = feedLikeRepository
                .existsByFeedIdAndMemberId(feedId, currentUserId);

        if (isLiked) {
            feedLikeRepository.deleteByFeedIdAndMemberId(feedId, currentUserId);
            feed.decreaseLikeCount();
        } else {
            FeedLike feedLike = FeedLike.builder()
                    .feed(feed)
                    .member(member)
                    .build();
            feedLikeRepository.save(feedLike);
            feed.increaseLikeCount();

            // 좋아요 알림 생성
            notificationService.createLikeNotification(
                    member,
                    feed.getMember(),
                    feed
            );
        }


        return FeedLikeResponse.of(feedId, feed.getLikeCount(), !isLiked);
    }

    // 좋아요한 피드 목록
    public Page<FeedResponse> getLikedFeeds(
            int page,
            int size,
            Long currentUserId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Feed> feeds = feedLikeRepository
                .findLikedFeedsByMemberId(currentUserId, pageable);

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
                    .existsByFeedIdAndMemberId(feed.getId(), currentUserId);
            return FeedResponse.from(feed, isLiked);
        });
    }

    // 공개 범위 검증
    private void validateVisibility(Feed feed, Long currentUserId) {
        switch (feed.getVisibility()) {
            case PRIVATE -> {
                if (!feed.getMember().getId().equals(currentUserId)) {
                    throw new IllegalArgumentException("비공개 피드입니다");
                }
            }
            case FOLLOWERS_ONLY -> {
                // 작성자 본인이 아닐 때만 팔로우 체크 진행
                if (!feed.getMember().getId().equals(currentUserId)) {

                    // 현재 로그인한 내 엔티티 찾기
                    Member me = memberRepository.findById(currentUserId)
                            .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다"));

                    //피드 작성자 엔티티 가져오기
                    Member blogger = feed.getMember();

                    //내가(follower) 글쓴이(following)를 팔로우하고 있는지 followRepository로 확인
                    boolean isFollowing = followRepository.existsByFollowerAndFollowing(me, blogger);

                    // 팔로우 상태가 아니라면 예외 발생
                    if (!isFollowing) {
                        throw new IllegalArgumentException("팔로워만 볼 수 있는 피드입니다");
                    }
                }
            }
            case PUBLIC -> {

            }
        }
    }
}
