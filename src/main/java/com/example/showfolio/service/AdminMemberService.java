package com.example.showfolio.service;

import com.example.showfolio.port.MemberRoleUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRoleUpdater updater;

    // TODO 회원 관련 기능 병합 되면 구현 진행
    public void updateRole() {
        updater.updateRole(null);
        throw new IllegalArgumentException("구현 필요");
    }
}
