package com.example.showfolio.port;

import com.example.showfolio.dto.AdminMemberDetailResponse;
import com.example.showfolio.dto.AdminMemberResponse;
import com.example.showfolio.dto.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 회원 목록을 조회 기능을 구현하기 위한 인터페이스 입니다.
 */
public interface MemberReader {

    AdminMemberDetailResponse getById(Long id);

    Page<AdminMemberResponse> getAll(MemberSearchCondition condition, Pageable pageable);
}
