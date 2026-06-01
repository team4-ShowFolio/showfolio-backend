package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.Member;
import com.sec01.showfilo.entity.SocialAccount;
import com.sec01.showfilo.enums.Provider;
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