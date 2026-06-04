package com.example.showfolio.service;

import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId)
            throws UsernameNotFoundException {

        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() ->
                        new UsernameNotFoundException("존재하지 않는 유저입니다"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(member.getId()))
                .password(member.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(
                        "ROLE_" + member.getRole().name())))
                .build();
    }
}