package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.User;
import com.sec01.showfilo.entity.UserTechStack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    List<UserTechStack> findByUser(User user);
    void deleteByUser(User user);
}
