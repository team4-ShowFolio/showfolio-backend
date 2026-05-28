package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.SocialAccount;
import com.sec01.showfilo.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderId(Provider provider, String providerId);
}
