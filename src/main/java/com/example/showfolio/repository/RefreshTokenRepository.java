package com.example.showfolio.repository;

import com.example.showfolio.entity.Member;
import com.example.showfolio.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // 토큰으로 찾기 (재발급)
    Optional<RefreshToken> findByToken(String token);

    // 유저로 찾기
    Optional<RefreshToken> findByMember(Member member);

    // 로그아웃할 때 삭제
    void deleteByMember(Member member);
}