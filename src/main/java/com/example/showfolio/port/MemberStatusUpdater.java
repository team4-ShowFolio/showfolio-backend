package com.example.showfolio.port;

/**
 * 회원 상태(정지/활성)를 수정하는 기능을 구현하기 위한 인터페이스입니다.
 */
public interface MemberStatusUpdater {

    // TODO Member 기능 병합 후 String status → MemberStatus 타입으로 변경
    void updateStatus(Long userId, String status);
}
