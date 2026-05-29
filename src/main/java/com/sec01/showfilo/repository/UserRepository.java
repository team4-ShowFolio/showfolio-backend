package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByNicknameAndDeletedAtIsNull(String nickname);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
