package com.example.showfolio.component;

import com.example.showfolio.dto.AdminMemberResponse;
import com.example.showfolio.dto.MemberSearchCondition;
import com.example.showfolio.port.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberReaderService implements MemberReader {

    @Override
    public AdminMemberResponse getById(Long userId) {
        // TODO Member 기능 병합 후 실제 회원 정보 조회로 대체
        return new AdminMemberResponse(userId, "TEST_NICKNAME", null, null, null, null, Instant.now(), List.of(), 0, 0);
    }

    @Override
    public Page<AdminMemberResponse> getAll(MemberSearchCondition condition, Pageable pageable) {
        // TODO Member 기능 병합 후 role, status 조건 필터링 구현
        return Page.empty(pageable);
    }
}
