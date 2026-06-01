package com.example.showfolio.component;

import com.example.showfolio.port.MemberStatusUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberStatusUpdaterService implements MemberStatusUpdater {

    @Override
    public void updateStatus(Long userId, String status) {
        // TODO 회원 기능 병합하면 구현 진행
    }
}
