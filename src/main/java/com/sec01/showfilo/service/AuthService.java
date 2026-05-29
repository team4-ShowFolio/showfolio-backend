package com.sec01.showfilo.service;

import com.sec01.showfilo.DTO.request.LoginRequest;
import com.sec01.showfilo.DTO.request.SignupRequest;
import com.sec01.showfilo.DTO.request.UpdateProfileRequest;
import com.sec01.showfilo.DTO.response.LoginResponse;
import com.sec01.showfilo.DTO.response.TokenResponse;
import com.sec01.showfilo.DTO.response.UserResponse;
import com.sec01.showfilo.entity.RefreshToken;
import com.sec01.showfilo.entity.User;
import com.sec01.showfilo.repository.RefreshTokenRepository;
import com.sec01.showfilo.repository.UserRepository;
import com.sec01.showfilo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponse signup(SignupRequest request){

        //1. 이메일 중복 확인
        if(userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())){
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        //2.닉네임 중복 확인
        if(userRepository.existsByNicknameAndDeletedAtIsNull(request.getNickname())){
            throw new RuntimeException("이미 사용중인 닉네임입니다.");
        }

        //3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setNickname(request.getNickname());

        userRepository.save(user);

        return new UserResponse(user);
    }

    @Transactional
    public LoginResponse login(@NonNull LoginRequest request){
        //1. 이메일로 유저 찾기
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(()->new RuntimeException("존재하지 않는 이메일입니다."));

        //2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        //3. 기존 리프레시토큰 삭제
        refreshTokenRepository.deleteByUser(user);

        //4. 토큰발급
        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        //4. Refresh Token DB 저장
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiredAt(LocalDateTime.now().plusWeeks(2));
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(accessToken, refreshToken, user);
    }

    @Transactional
    public void logout(Long userId){

        //1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        //2. DB에서 Refresh Token삭제
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken){

        //1. Refresh Token 유효성 검증
        if(!jwtUtil.validateToken(refreshToken)){
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        //2. DB에서 Refresh Token 찾기
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(()->new RuntimeException("존재하지 않는 Refresh Token입니다."));

        //3. 만료시간 확인
        if(refreshTokenEntity.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("만료된 Refresh Token입니다.");
        }

        //4. 유저 가지고오기
        User user = refreshTokenEntity.getUser();

        //5. 새 Access Token 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getId());

        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId){
        //1. 유저찾기
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 유저입니다."));

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request){

        //1. 유저찾기
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("존재하지 않는 유저입니다."));

        //2. 닉네임 변경시 중복 확인
        if(request.getNickname() != null &&
        !request.getNickname().equals(user.getNickname()) &&
                userRepository.existsByEmailAndDeletedAtIsNull(request.getNickname())){
            throw new RuntimeException("이미 사용중인 닉네임입니다.");
        }

        //3. 수정
        if(request.getNickname() != null){
            user.setNickname(request.getNickname());
        }
        if(request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if(request.getProfileImage() != null){
            user.setProfileImage(request.getProfileImage());
        }

        userRepository.save(user);

        return new UserResponse(user);
    }

    @Transactional
    public void deleteAccount(Long userId){

        //1. 유저 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("존재하지 않는 유저입니다."));

        //2. Refresh Token 삭제
        refreshTokenRepository.deleteByUser(user);

        //3. 소프트 삭제
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
