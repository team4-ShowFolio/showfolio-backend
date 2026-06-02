package com.example.showfolio.component;

import com.example.showfolio.mock.Member;
import com.example.showfolio.mock.MemberRepository;
import com.example.showfolio.mock.Role;
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
        // TODO 회원 기능 병합 후 orElseGet(Member::new) → orElseThrow로 변경
        Member member = memberRepository.findById(userId)
                .orElseGet(Member::new);

        member.updateRole(role);
        memberRepository.save(member);
    }
}
