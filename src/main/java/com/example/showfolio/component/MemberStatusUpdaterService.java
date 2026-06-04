package com.example.showfolio.component;

import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.port.MemberStatusUpdater;
import java.time.LocalDateTime;
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

        member.setDeletedAt(LocalDateTime.now());
    }
}
