package com.sec01.showfilo.service;

import com.sec01.showfilo.DTO.request.SignupRequest;
import com.sec01.showfilo.DTO.response.UserResponse;
import com.sec01.showfilo.entity.User;
import com.sec01.showfilo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request){

        //1. 이메일 중복 확인
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        //2.닉네임 중복 확인
        if(userRepository.existsByNickname(request.getNickname())){
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
}
