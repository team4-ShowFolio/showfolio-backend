package com.example.showfolio.component;

import com.example.showfolio.port.MemberRoleUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRoleUpdaterService implements MemberRoleUpdater {

    @Override
    public void updateRole(Long userId) {
        // TODO 회원 기능 병합하면 구현 진행
    }
}
