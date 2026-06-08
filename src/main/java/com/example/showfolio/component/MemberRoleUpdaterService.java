package com.example.showfolio.component;

import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.enums.Role;
import com.example.showfolio.port.MemberRoleUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberRoleUpdaterService implements MemberRoleUpdater {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void updateRole(Long userId, Role role) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.setRole(role);
        memberRepository.save(member);
    }
}
