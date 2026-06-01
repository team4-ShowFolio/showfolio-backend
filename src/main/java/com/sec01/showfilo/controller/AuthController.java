package com.sec01.showfilo.controller;

import com.sec01.showfilo.DTO.request.LoginRequest;
import com.sec01.showfilo.DTO.request.SignupRequest;
import com.sec01.showfilo.DTO.request.TechStackRequest;
import com.sec01.showfilo.DTO.request.UpdateProfileRequest;
import com.sec01.showfilo.DTO.response.LoginResponse;
import com.sec01.showfilo.DTO.response.MemberResponse;
import com.sec01.showfilo.DTO.response.TechStackResponse;
import com.sec01.showfilo.DTO.response.TokenResponse;
import com.sec01.showfilo.service.AuthService;
import com.sec01.showfilo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}