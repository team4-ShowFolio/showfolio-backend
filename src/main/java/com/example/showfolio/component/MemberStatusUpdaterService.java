package com.example.showfolio.component;

import com.example.showfolio.mock.Member;
import com.example.showfolio.mock.MemberRepository;
import com.example.showfolio.port.MemberStatusUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberStatusUpdaterService implements MemberStatusUpdater {

    private final MemberRepository memberRepository;

    @Override
    public void updateStatus(Long userId, String status) {
        // TODO 회원 기능 병합하면 구현 진행
        Member member = memberRepository.findById(userId)
                .orElseGet(Member::new);

        member.updateDelete();
    }
}
