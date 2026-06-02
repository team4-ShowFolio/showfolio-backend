package com.example.showfolio.service;

import com.example.showfolio.dto.AdminMemberResponse;
import com.example.showfolio.dto.MemberRoleUpdateRequest;
import com.example.showfolio.dto.MemberSearchCondition;
import com.example.showfolio.dto.MemberSuspendRequest;
import com.example.showfolio.port.MemberReader;
import com.example.showfolio.port.MemberRoleUpdater;
import com.example.showfolio.port.MemberStatusUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// TODO 회원 관련 기능 병합 되면 구현 진행
@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRoleUpdater roleUpdater;
    private final MemberStatusUpdater statusUpdater;
    private final MemberReader reader;

    public void updateRole(Long id, MemberRoleUpdateRequest request) {
        roleUpdater.updateRole(id, request.role());
    }

    public void updateStatus(Long id, MemberSuspendRequest request) {
        statusUpdater.updateStatus(id, request.status());
    }

    public AdminMemberResponse getById(Long id) {
        return reader.getById(id);
    }

    public Page<AdminMemberResponse> getAll(MemberSearchCondition condition, Pageable pageable) {
        return reader.getAll(condition, pageable);
    }
}
