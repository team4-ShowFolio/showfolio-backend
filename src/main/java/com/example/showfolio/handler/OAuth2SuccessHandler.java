package com.example.showfolio.handler;

import com.example.showfolio.entity.Member;
import com.example.showfolio.entity.RefreshToken;
import com.example.showfolio.entity.SocialAccount;
import com.example.showfolio.enums.Provider;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.repository.RefreshTokenRepository;
import com.example.showfolio.repository.SocialAccountRepository;
import com.example.showfolio.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialAccountRepository socialAccountRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. providerId로 member 찾기
        String providerId = String.valueOf(oAuth2User.getAttributes().get("id"));

        SocialAccount socialAccount = socialAccountRepository
                .findByProviderAndProviderId(Provider.GITHUB, providerId)
                .orElseThrow(() -> new RuntimeException("소셜 계정을 찾을 수 없습니다"));

        Member member = socialAccount.getMember();

        // 2. 기존 RefreshToken 삭제
        refreshTokenRepository.deleteByMember(member);

        // 3. 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(member.getId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getId());

        // 4. RefreshToken 저장
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setMember(member);
        refreshTokenEntity.setExpiredAt(LocalDateTime.now().plusWeeks(2));
        refreshTokenRepository.save(refreshTokenEntity);

//        // 5. 프론트엔드로 토큰 전달
//        String redirectUrl = "http://localhost:3000/oauth2/callback" +
//                "?accessToken=" + accessToken +
//                "&refreshToken=" + refreshToken;
//
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{" +
                        "\"accessToken\":\"" + accessToken + "\"," +
                        "\"refreshToken\":\"" + refreshToken + "\"," +
                        "\"memberId\":" + member.getId() +
                        "}"
        );

    }
}