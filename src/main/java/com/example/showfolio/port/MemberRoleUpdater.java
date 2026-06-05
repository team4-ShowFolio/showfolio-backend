package com.example.showfolio.port;

import com.example.showfolio.enums.Role;

/**
 * 회원 권한을 수정하는 기능을 구현하기 위한 인터페이스 입니다.
 *
 */
public interface MemberRoleUpdater {

    // TODO Member 기능 병합 후 String role → MemberRole 타입으로 변경
    void updateRole(Long userId, Role role);

}
