package com.example.showfolio.component;

import com.example.showfolio.entity.Member;
import com.example.showfolio.enums.MemberStatus;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.port.MemberStatusUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberStatusUpdaterService implements MemberStatusUpdater {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void updateStatus(Long userId, MemberStatus status) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.delete();
        memberRepository.save(member);
    }
}
