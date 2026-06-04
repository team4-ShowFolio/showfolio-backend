package com.example.showfolio.component;

import com.example.showfolio.dto.AdminMemberDetailResponse;
import com.example.showfolio.dto.AdminMemberResponse;
import com.example.showfolio.dto.MemberSearchCondition;
import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.port.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberReaderService implements MemberReader {

    private final MemberRepository memberRepository;

    @Override
    public AdminMemberDetailResponse getById(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseGet(Member::new);

        // TODO Feed, Comment 개수 및 목록 조회 필요
        return new AdminMemberDetailResponse(
                member.getId(), member.getNickname(), member.getEmail(), member.getEmail(),
                member.getRole(), null, member.getCreatedAt(),
                List.of(), 0, 0,
                List.of(), List.of(), List.of()
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
