package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 찾기 (로그인)
    Optional<Member> findByEmail(String email);

    // 이메일 중복 확인 (회원가입)
    boolean existsByEmail(String email);

    // 닉네임 중복 확인 (회원가입)
    boolean existsByNickname(String nickname);

    // 탈퇴한 유저 제외하고 이메일로 찾기
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

    // 탈퇴한 유저 제외하고 닉네임 중복 확인
    boolean existsByNicknameAndDeletedAtIsNull(String nickname);

    // 탈퇴한 유저 제외하고 이메일 중복 확인
    boolean existsByEmailAndDeletedAtIsNull(String email);
}