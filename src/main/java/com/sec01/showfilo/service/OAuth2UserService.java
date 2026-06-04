package com.sec01.showfilo.service;

import com.sec01.showfilo.entity.Member;
import com.sec01.showfilo.entity.SocialAccount;
import com.sec01.showfilo.enums.Provider;
import com.sec01.showfilo.repository.MemberRepository;
import com.sec01.showfilo.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        // 1. 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. provider 확인 (github)
        String provider = userRequest.getClientRegistration()
                .getRegistrationId().toUpperCase();

        // 3. GitHub 유저 정보 추출
        String providerId = String.valueOf(oAuth2User.getAttributes().get("id"));
        String email = (String) oAuth2User.getAttributes().get("email");
        String nickname = (String) oAuth2User.getAttributes().get("login");
        String profileImage = (String) oAuth2User.getAttributes().get("avatar_url");

        // 4. 기존 소셜 계정 찾기
        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderId(
                        Provider.valueOf(provider), providerId)
                .orElse(null);

        Member member;

        if (socialAccount == null) {
            // 5. 신규 회원 → Member 생성
            member = new Member();
            member.setEmail(email != null ? email : nickname + "@github.com");
            member.setNickname(generateUniqueNickname(nickname));
            member.setProfileImage(profileImage);

            memberRepository.save(member);

            // 6. SocialAccount 저장
            SocialAccount newSocialAccount = new SocialAccount();
            newSocialAccount.setProvider(Provider.valueOf(provider));
            newSocialAccount.setProviderId(providerId);
            newSocialAccount.setMember(member);
            socialAccountRepository.save(newSocialAccount);

        } else {
            // 7. 기존 회원 → Member 가져오기
            member = socialAccount.getMember();
        }

        return oAuth2User;
    }

    // 닉네임 중복 방지
    private String generateUniqueNickname(String nickname) {
        String uniqueNickname = nickname;
        int count = 1;
        while (memberRepository.existsByNicknameAndDeletedAtIsNull(uniqueNickname)) {
            uniqueNickname = nickname + count;
            count++;
        }
        return uniqueNickname;
    }
}