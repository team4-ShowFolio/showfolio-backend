package com.example.showfolio.repository;

import com.example.showfolio.entity.Member;
import com.example.showfolio.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {

    // 유저의 기술스택 목록
    List<UserTechStack> findByMember(Member member);

    // 유저의 기술스택 삭제
    void deleteByMember(Member member);
}