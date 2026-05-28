package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.RefreshToken;
import com.sec01.showfilo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user); //로그아웃시 토큰 삭제
}
