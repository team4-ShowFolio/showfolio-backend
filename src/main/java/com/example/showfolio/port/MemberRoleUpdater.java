package com.example.showfolio.port;

/**
 * 회원 권한을 수정하는 기능을 구현하기 위한 인터페이스 입니다.
 *
 */
public interface MemberRoleUpdater {

    void updateRole(Long userId);

}
