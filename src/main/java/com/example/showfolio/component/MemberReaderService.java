package com.example.showfolio.component;

import com.example.showfolio.dto.AdminMemberDetailResponse;
import com.example.showfolio.dto.AdminMemberResponse;
import com.example.showfolio.dto.MemberSearchCondition;
import com.example.showfolio.dto.RecentCommentResponse;
import com.example.showfolio.dto.RecentFeedResponse;
import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.CommentRepository;
import com.example.showfolio.repository.FeedRepository;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.port.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberReaderService implements MemberReader {

    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

    @Override
    public AdminMemberDetailResponse getById(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        long feedCount = feedRepository.countByMemberId(userId);
        int commentCount = (int) commentRepository.countByMemberIdAndDeletedAtIsNull(userId);

        List<RecentFeedResponse> recentFeeds = feedRepository.findTop5ByMemberIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(f -> new RecentFeedResponse(
                        f.getId(),
                        f.getTitle(),
                        f.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                ))
                .toList();

        List<RecentCommentResponse> recentComments = commentRepository.findTop5ByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(c -> new RecentCommentResponse(
                        c.getId(),
                        c.getContent(),
                        c.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()
                ))
                .toList();

        return new AdminMemberDetailResponse(
                member.getId(), member.getNickname(), member.getEmail(), member.getEmail(),
                member.getRole(), null, member.getCreatedAt(),
                List.of(), feedCount, commentCount,
                List.of(), recentFeeds, recentComments
        );
    }

    @Override
    public Page<AdminMemberResponse> getAll(MemberSearchCondition condition, Pageable pageable) {
        Specification<Member> spec = (root, query, cb) -> null;

        if (condition.keyword() != null) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(root.get("nickname"), "%" + condition.keyword() + "%"),
                    cb.like(root.get("email"), "%" + condition.keyword() + "%")
            ));
        }
        if (condition.role() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), condition.role()));
        }
        if (condition.status() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), condition.status()));
        }

        return memberRepository.findAll(spec, pageable)
                .map(m -> new AdminMemberResponse(
                        m.getId(),
                        m.getNickname(),
                        m.getEmail(),
                        m.getEmail(),
                        m.getRole(),
                        null,
                        m.getCreatedAt(),
                        List.of(),
                        0,
                        0
                ));
    }
}
