package com.example.showfolio.service;

import com.example.showfolio.dto.request.LoginRequest;
import com.example.showfolio.dto.request.SignupRequest;
import com.example.showfolio.dto.request.TechStackRequest;
import com.example.showfolio.dto.request.UpdateProfileRequest;
import com.example.showfolio.dto.response.LoginResponse;
import com.example.showfolio.dto.response.MemberResponse;
import com.example.showfolio.dto.response.TechStackResponse;
import com.example.showfolio.dto.response.TokenResponse;
import com.example.showfolio.entity.Member;
import com.example.showfolio.entity.RefreshToken;
import com.example.showfolio.entity.UserTechStack;
import com.example.showfolio.enums.SubscriptionType;
import com.example.showfolio.repository.MemberRepository;
import com.example.showfolio.repository.RefreshTokenRepository;
import com.example.showfolio.repository.UserTechStackRepository;
import com.example.showfolio.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public MemberResponse signup(SignupRequest request) {

        // 1. 이메일 중복 확인
        if (memberRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new RuntimeException("이미 사용중인 이메일입니다");
        }

        // 2. 닉네임 중복 확인
        if (memberRepository.existsByNicknameAndDeletedAtIsNull(request.getNickname())) {
            throw new RuntimeException("이미 사용중인 닉네임입니다");
        }

        // 3. Member 생성
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setNickname(request.getNickname());
        member.setProfileImage(request.getProfileImage());
        member.setBio(request.getBio());
        memberRepository.save(member);

        return new MemberResponse(member);
    }

    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {

        // 1. 이메일로 유저 찾기 (탈퇴한 유저 제외)
        Member member = memberRepository
                .findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다"));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다");
        }

        // 3. 기존 RefreshToken 삭제
        refreshTokenRepository.deleteByMember(member);

        // 4. 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(member.getId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getId());

        // 5. RefreshToken 저장
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setMember(member);
        refreshTokenEntity.setExpiredAt(LocalDateTime.now().plusWeeks(2));
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(accessToken, refreshToken, member);
    }

    // 로그아웃
    @Transactional
    public void logout(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        refreshTokenRepository.deleteByMember(member);
    }

    // 토큰 재발급
    @Transactional
    public TokenResponse refresh(String refreshToken) {

        // 1. 토큰 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다");
        }

        // 2. DB에서 토큰 찾기
        RefreshToken refreshTokenEntity = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다"));

        // 3. 만료시간 확인
        if (refreshTokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("만료된 토큰입니다");
        }

        // 4. 새 AccessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(
                refreshTokenEntity.getMember().getId());

        return new TokenResponse(newAccessToken, refreshToken);
    }

    // 프로필 조회
    @Transactional(readOnly = true)
    public MemberResponse getProfile(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        return new MemberResponse(member);
    }

    // 프로필 수정
    @Transactional
    public MemberResponse updateProfile(Long memberId, UpdateProfileRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 닉네임 중복 확인
        if (request.getNickname() != null &&
                !request.getNickname().equals(member.getNickname()) &&
                memberRepository.existsByNicknameAndDeletedAtIsNull(request.getNickname())) {
            throw new RuntimeException("이미 사용중인 닉네임입니다");
        }

        if (request.getNickname() != null) {
            member.setNickname(request.getNickname());
        }
        if (request.getProfileImage() != null) {
            member.setProfileImage(request.getProfileImage());
        }
        if (request.getBio() != null) {
            member.setBio(request.getBio());
        }

        memberRepository.save(member);
        return new MemberResponse(member);
    }

    // 기술스택 수정
    @Transactional
    public List<TechStackResponse> updateTechStack(
            Long memberId, TechStackRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 기존 기술스택 삭제
        userTechStackRepository.deleteByMember(member);

        // 새 기술스택 저장
        List<UserTechStack> techStacks = request.getTechNames().stream()
                .map(techName -> {
                    UserTechStack techStack = new UserTechStack();
                    techStack.setTechName(techName);
                    techStack.setMember(member);
                    return techStack;
                })
                .collect(Collectors.toList());

        userTechStackRepository.saveAll(techStacks);

        return techStacks.stream()
                .map(TechStackResponse::new)
                .collect(Collectors.toList());
    }

    // 기술스택 조회
    @Transactional(readOnly = true)
    public List<TechStackResponse> getTechStack(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        return userTechStackRepository.findByMember(member)
                .stream()
                .map(TechStackResponse::new)
                .collect(Collectors.toList());
    }

    // 회원탈퇴
    @Transactional
    public void deleteAccount(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 관련 데이터 삭제
        refreshTokenRepository.deleteByMember(member);
        userTechStackRepository.deleteByMember(member);

        // 소프트 삭제
        member.setDeletedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    // 이메일 중복확인
    public boolean checkEmail(String email){
        return memberRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    // 닉네임 중복확인
    public boolean checkNickname(String nickname){
        return memberRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    }

    //다른 유저 프로필 조회
    @Transactional(readOnly = true)
    public MemberResponse getMemberProfile(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new RuntimeException("존재하지 않는 유저입니다."));
        return new MemberResponse(member);
    }

    // PRO (AI 기능) 구독 상태 변경
    @Transactional
    public MemberResponse changeSubscription(Long memberId, SubscriptionType type) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 전달받은 타입("PREMIUM" 또는 "FREE")에 맞게 엔티티 상태 변경
        member.setSubscriptionType(type);

        member.setSubscriptionExpiredAt(LocalDateTime.now().plusMonths(1));

        memberRepository.save(member);
        return new MemberResponse(member); // 갱신된 회원 객체 반환
    }
}