package com.example.showfolio.controller;

import com.example.showfolio.dto.request.LoginRequest;
import com.example.showfolio.dto.request.SignupRequest;
import com.example.showfolio.dto.request.TechStackRequest;
import com.example.showfolio.dto.request.UpdateProfileRequest;
import com.example.showfolio.enums.SubscriptionType;
import com.example.showfolio.service.AuthService;
import com.example.showfolio.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        authService.logout(memberId);
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader("Authorization") String token) {
        String refreshToken = token.substring(7);
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    // 내 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(authService.getProfile(memberId));
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileRequest request) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(authService.updateProfile(memberId, request));
    }

    // 기술스택 조회
    @GetMapping("/techstack")
    public ResponseEntity<?> getTechStack(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(authService.getTechStack(memberId));
    }

    // 기술스택 수정
    @PutMapping("/techstack")
    public ResponseEntity<?> updateTechStack(
            @RequestHeader("Authorization") String token,
            @RequestBody TechStackRequest request) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(authService.updateTechStack(memberId, request));
    }

    // 회원탈퇴
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        authService.deleteAccount(memberId);
        return ResponseEntity.ok("회원탈퇴 성공");
    }

    //이메일 중복 확인
    @GetMapping("/check/email")
    public ResponseEntity<?> checkEmail(@RequestParam String email){
        boolean isDuplicate = authService.checkEmail(email);
        return ResponseEntity.ok(isDuplicate ?
                "이미 사용중인 이메일입니다." : "사용 가능한 이메일입니다.");
    }

    //닉네임 중복확인
    @GetMapping("/check/nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname){
        boolean isDuplicate = authService.checkNickname(nickname);
        return ResponseEntity.ok(isDuplicate ?
                "이미 사용중인 닉네임입니다." : "사용 가능한 닉네임입니다.");
    }

    //다른 유저 프로필 조회
    @GetMapping("/profile/{memberId}")
    public ResponseEntity<?> getMemberProfile(
            @PathVariable Long memberId ) {
        return ResponseEntity.ok(authService.getMemberProfile(memberId));
    }

    // PRO (AI 기능) 구독하기
    @PutMapping("/subscribe")
    public ResponseEntity<?> subscribeMember(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(authService.changeSubscription(memberId, SubscriptionType.PREMIUM));
    }

    // PRO 구독 해지
    @PutMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeMember(
            @RequestHeader("Authorization") String token) {
        Long memberId = jwtUtil.getUserId(token.substring(7));
        return ResponseEntity.ok(authService.changeSubscription(memberId, SubscriptionType.FREE));
    }
}