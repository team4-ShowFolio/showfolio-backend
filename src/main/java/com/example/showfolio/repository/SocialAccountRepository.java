package com.example.showfolio.repository;

import com.example.showfolio.entity.Member;
import com.example.showfolio.entity.SocialAccount;
import com.example.showfolio.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    // 소셜로그인 찾기
    Optional<SocialAccount> findByProviderAndProviderId(
            Provider provider, String providerId);

    // 유저의 소셜로그인 목록
    List<SocialAccount> findByMember(Member member);

    // 유저의 소셜로그인 삭제
    void deleteByMember(Member member);
}