package com.sec01.showfilo.controller;

import com.sec01.showfilo.DTO.request.LoginRequest;
import com.sec01.showfilo.DTO.request.SignupRequest;
import com.sec01.showfilo.DTO.response.LoginResponse;
import com.sec01.showfilo.DTO.response.UserResponse;
import com.sec01.showfilo.repository.UserRepository;
import com.sec01.showfilo.service.AuthService;
import com.sec01.showfilo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> singup(@RequestBody SignupRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token){
        Long userId = jwtUtil.getUserId(token.substring(7));
        authService.logout(userId);
        return ResponseEntity.ok("로그아웃 성공");
    }
}
